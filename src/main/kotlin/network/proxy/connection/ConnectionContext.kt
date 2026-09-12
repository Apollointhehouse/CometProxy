package dev.apollointhehouse.network.proxy.connection

import dev.apollointhehouse.network.extensions.close
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.proxy.session.PlayerSession
import io.ktor.network.sockets.Connection
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.io.IOException
import org.apache.logging.log4j.kotlin.logger
import kotlin.uuid.Uuid

data class ConnectionContext(
    val clientConn: Connection,
    val serverConn: Connection,
    var session: PlayerSession? = null,
    val id: Uuid = Uuid.random()
) : AutoCloseable {
    private val log = logger()
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val clientQueue = Channel<Packet>(Channel.UNLIMITED)
    private val serverQueue = Channel<Packet>(Channel.UNLIMITED)

    init {
        ioScope.launch { writerLoop(clientConn.output, clientQueue, "client") }
        ioScope.launch { writerLoop(serverConn.output, serverQueue, "server") }
    }

    fun sendToClient(packet: Packet) {
        clientQueue.trySend(packet).onFailure {
            log.warn { "Failed to enqueue ${packet::class.simpleName} for client" }
        }
    }

    fun sendToServer(packet: Packet) {
        serverQueue.trySend(packet).onFailure {
            log.warn { "Failed to enqueue ${packet::class.simpleName} for server" }
        }
    }

    suspend fun sendToClientImmediately(packet: Packet) {
        Packet.writePacket(clientConn.output, packet)
    }

    suspend fun sendToServerImmediately(packet: Packet) {
        Packet.writePacket(serverConn.output, packet)
    }

    private suspend fun writerLoop(channel: ByteWriteChannel, queue: Channel<Packet>, target: String) {
        try {
            while (ioScope.isActive) {
                val firstPacket = queue.receiveCatching().getOrNull() ?: break

                try {
                    Packet.writePacket(channel, firstPacket)
                    channel.flush()
                } catch (_: ClosedWriteChannelException) {
                    log.debug { "Dropped packet stream: $target channel already closed" }
                    break
                } catch (e: IOException) {
                    log.warn(e) { "Failed to write batch to $target" }
                }
            }
        } finally {
            queue.close()
        }
    }


    override fun close() {
        clientQueue.close()
        serverQueue.close()
        clientConn.close()
        serverConn.close()
        ioScope.cancel()
    }
}