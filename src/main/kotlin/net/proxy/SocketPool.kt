package dev.apollointhehouse.net.proxy

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import org.apache.logging.log4j.kotlin.logger

class SocketPool(
    private val address: InetSocketAddress,
    private val size: Int,
    private val selectorManager: SelectorManager
) {
    private val log = logger("SocketPool")
    private val channel: Channel<Socket> = Channel(Channel.UNLIMITED)
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    suspend fun init() {
        repeat(size) { replenish() }
    }

    fun getSocket(): Socket? {
        val socket = channel.tryReceive().getOrNull()
        scope.launch { replenish() }
        return socket
    }

    private suspend fun createSocket(): Socket? {
        try {
            return aSocket(selectorManager).tcp().connect(address)
        } catch (e: Exception) {
            log.error(e) { "Failed to connect to $address" }
            return null
        }
    }

    private suspend fun replenish() {
        val socket = createSocket() ?: return

        val result = channel.trySend(socket)

        if (result.isFailure) {
            log.error(result.exceptionOrNull()) { "Failed to queue socket" }
        }
    }

    suspend fun close() = withContext(Dispatchers.IO) {
        channel.close()
        while (true) {
            val socket = channel.tryReceive().getOrNull() ?: break
            socket.close()
        }
        scope.cancel()
    }
}