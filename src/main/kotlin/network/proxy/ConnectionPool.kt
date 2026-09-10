package dev.apollointhehouse.network.proxy

import dev.apollointhehouse.network.extensions.close
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.handshake.PacketDisconnect
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.kotlin.logger
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

class ConnectionPool(
    private val address: InetSocketAddress,
    private val size: Int,
    private val selectorManager: SelectorManager,
    private val healthCheck: Duration = 15.seconds
) {
    private val log = logger("SocketPool")
    private val channel: Channel<Connection> = Channel(Channel.UNLIMITED)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var healthCheckJob: Job? = null

    suspend fun init() = coroutineScope {
        (1..size).map {
            delay(1.seconds)
            async { replenish() }
        }.awaitAll()
        healthCheckJob = scope.launch { healthCheckLoop() }
    }

    suspend fun getConnection(): Connection? {
        while (true) {
            val connection = channel.tryReceive().getOrNull() ?: break
            scope.launch { replenish() }

            if (!wasKicked(connection)) return connection
        }

        return channel.receiveCatching().getOrNull()
    }

    private suspend fun createConnection(): Connection? {
        try {
            val conn = aSocket(selectorManager).tcp().connect(address) {
                keepAlive = true
            }
            return conn.connection()
        } catch (e: Exception) {
            log.error(e) { "Failed to connect to $address" }
            return null
        }
    }

    private suspend fun replenish() = withContext(Dispatchers.IO) {
        val connection = createConnection() ?: return@withContext
        val result = channel.trySend(connection)

        if (result.isFailure) {
            log.error(result.exceptionOrNull()) { "Failed to queue connection" }
            connection.close()
        }
    }

    private suspend fun healthCheckLoop() {
        while (scope.isActive) {
            delay(healthCheck)
            runCatching { sweep() }
                .onFailure { log.error(it) { "Health check sweep failed" } }
        }
    }

    private suspend fun wasKicked(connection: Connection): Boolean {
        if (connection.input.availableForRead > 0) {
            val packet = Packet.readPacket(connection.input)
            if (packet is PacketDisconnect) {
                log.debug { "Kick reason: ${packet.reason}" }
                return true
            }
        }

        return false
    }

    private suspend fun isAlive(connection: Connection): Boolean {
        if (connection.socket.isClosed) return false

        try {
            if (wasKicked(connection)) {
                logger.debug { "Server closed connection" }
                return false
            }

            if (connection.input.isClosedForRead) return false

            return true
        } catch (e: Exception) {
            log.warn(e) { "Health probe failed for connection to $address" }
            return false
        }
    }

    private suspend fun sweep() = withContext(Dispatchers.IO) {
        val drained = generateSequence { channel.tryReceive().getOrNull() }.toList()
        var dead = 0

        for (conn in drained) {
            if (isAlive(conn)) {
                if (channel.trySend(conn).isFailure) conn.close()
            } else {
                dead++
                conn.close()
            }
        }

        if (dead > 0) {
            log.debug { "Health check evicted $dead dead connection(s), replenishing" }
            coroutineScope { (1..dead).map { async { replenish() } }.awaitAll() }
        }
    }

    suspend fun close() = withContext(Dispatchers.IO) {
        healthCheckJob?.cancel()
        channel.close()
        while (true) {
            val connection = channel.tryReceive().getOrNull() ?: break
            connection.close()
        }
        scope.cancel()
    }
}