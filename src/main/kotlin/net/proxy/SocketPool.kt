package dev.apollointhehouse.net.proxy

import dev.apollointhehouse.net.packet.Packet
import dev.apollointhehouse.net.packet.PacketDisconnect
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.kotlin.logger
import kotlin.time.Duration
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
    private val healthCheck: Duration = 15.seconds
) {
    private val log = logger("SocketPool")
    private val channel: Channel<PooledSocket> = Channel(Channel.UNLIMITED)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var healthCheckJob: Job? = null

    suspend fun init() = coroutineScope {
        (1..size).map {
            delay(1.seconds)
            async { replenish() }
        }.awaitAll()
        healthCheckJob = scope.launch { healthCheckLoop() }
    }

    suspend fun getSocket(): PooledSocket? {
        while (true) {
            val socket = channel.tryReceive().getOrNull() ?: break
            scope.launch { replenish() }

            if (!wasKicked(socket)) return socket
        }

        return null
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
        val socket = createSocket() ?: return@withContext
        val result = channel.trySend(socket)

        if (result.isFailure) {
            log.error(result.exceptionOrNull()) { "Failed to queue socket" }
            socket.close()
        }
    }

    private suspend fun healthCheckLoop() {
        while (scope.isActive) {
            delay(healthCheck)
            runCatching { sweep() }
                .onFailure { log.error(it) { "Health check sweep failed" } }
        }
    }

    private suspend fun wasKicked(socket: PooledSocket): Boolean {
        if (socket.read.availableForRead > 0) {
            val packet = Packet.readPacket(socket.read)
            if (packet is PacketDisconnect) {
                log.debug { "Kick reason: ${packet.reason}" }
                return true
            }
        }

        return false
    }

    private suspend fun isAlive(socket: PooledSocket): Boolean {
        if (socket.isClosed) return false

        try {
            if (wasKicked(socket)) {
                logger.debug { "Server closed socket" }
                return false
            }

            if (socket.read.isClosedForRead) return false

            return true
        } catch (e: Exception) {
            log.warn(e) { "Health probe failed for socket to $address" }
            return false
        }
    }

    private suspend fun sweep() = withContext(Dispatchers.IO) {
        val drained = generateSequence { channel.tryReceive().getOrNull() }.toList()
        var dead = 0

        for (socket in drained) {
            if (isAlive(socket)) {
                if (channel.trySend(socket).isFailure) socket.close()
            } else {
                dead++
                socket.close()
            }
        }

        if (dead > 0) {
            log.debug { "Health check evicted $dead dead socket(s), replenishing" }
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