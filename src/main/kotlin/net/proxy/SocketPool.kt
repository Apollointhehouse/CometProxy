package dev.apollointhehouse.net.proxy

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.kotlin.logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

data class PooledSocket(
    private val socket: Socket,
    val read: ByteReadChannel,
    val write: ByteWriteChannel,
) : Socket by socket {
    override fun attachForReading(channel: ByteChannel): WriterJob {
        throw UnsupportedOperationException("PooledSocket manages its own channels. Direct attachment is disabled.")
    }

    override fun attachForWriting(channel: ByteChannel): ReaderJob {
        throw UnsupportedOperationException("PooledSocket manages its own channels. Direct attachment is disabled.")
    }
}

class SocketPool(
    private val address: InetSocketAddress,
    private val size: Int,
    private val selectorManager: SelectorManager,
    private val healthCheck: Duration = 30.seconds,
    private val healthProbe: Duration = 200.milliseconds,
) {
    private val log = logger("SocketPool")
    private val channel: Channel<PooledSocket> = Channel(Channel.UNLIMITED)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var healthCheckJob: Job? = null

    suspend fun init() = coroutineScope {
        (1..size).map { async { replenish() } }.awaitAll()
        healthCheckJob = scope.launch { healthCheckLoop() }
    }

    fun getSocket(): PooledSocket? {
        val socket = channel.tryReceive().getOrNull()
        scope.launch { replenish() }
        return socket
    }

    private suspend fun createSocket(): PooledSocket? {
        try {
            val socket = aSocket(selectorManager).tcp().connect(address) {
                keepAlive = true
            }
            return PooledSocket(socket, socket.openReadChannel(), socket.openWriteChannel())
        } catch (e: Exception) {
            log.error(e) { "Failed to connect to $address" }
            return null
        }
    }

    private suspend fun replenish() = withContext(Dispatchers.IO) {
        val pooled = createSocket() ?: return@withContext
        val result = channel.trySend(pooled)

        if (result.isFailure) {
            log.error(result.exceptionOrNull()) { "Failed to queue socket" }
            pooled.close()
        }
    }

    private suspend fun healthCheckLoop() {
        while (scope.isActive) {
            delay(healthCheck)
            runCatching { sweep() }
                .onFailure { log.error(it) { "Health check sweep failed" } }
        }
    }

    private suspend fun isAlive(pooled: PooledSocket): Boolean {
        if (pooled.isClosed) return false

        try {
            withTimeoutOrNull(healthProbe) {
                pooled.read.awaitContent()
            } ?: return true

            if (pooled.read.isClosedForRead) return false

            log.warn { "Pooled socket to $address had unexpected pending data, discarding" }
            return false
        } catch (e: Exception) {
            log.warn(e) { "Health probe failed for socket to $address" }
            return false
        }
    }

    private suspend fun sweep() = withContext(Dispatchers.IO) {
        val drained = generateSequence { channel.tryReceive().getOrNull() }.toList()
        var dead = 0

        for (pooled in drained) {
            if (isAlive(pooled)) {
                if (channel.trySend(pooled).isFailure) pooled.close()
            } else {
                dead++
                pooled.close()
            }
        }

        if (dead > 0) {
            log.warn { "Health check evicted $dead dead socket(s), replenishing" }
            coroutineScope { (1..dead).map { async { replenish() } }.awaitAll() }
        }
    }

    suspend fun close() = withContext(Dispatchers.IO) {
        healthCheckJob?.cancel()
        channel.close()
        while (true) {
            val pooled = channel.tryReceive().getOrNull() ?: break
            pooled.close()
        }
        scope.cancel()
    }
}