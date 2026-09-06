package dev.apollointhehouse.net.proxy.pipeline

import dev.apollointhehouse.net.packet.Packet
import dev.apollointhehouse.net.proxy.session.PlayerSession
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.io.IOException
import org.apache.logging.log4j.kotlin.logger
import kotlin.uuid.Uuid

private val log = logger("NetContext")

data class ConnectionContext(
    val serverOut: ByteWriteChannel,
    val clientOut: ByteWriteChannel,
    var session: PlayerSession? = null,
    val id: Uuid = Uuid.random()
) : AutoCloseable {
    private val ioScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private val clientQueue = Channel<Packet>(Channel.UNLIMITED)
    private val serverQueue = Channel<Packet>(Channel.UNLIMITED)

    init {
        ioScope.launch { writerLoop(clientOut, clientQueue, "client") }
        ioScope.launch { writerLoop(serverOut, serverQueue, "server") }
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

    private suspend fun writerLoop(channel: ByteWriteChannel, queue: Channel<Packet>, target: String) {
        try {
            while (ioScope.isActive) {
                val firstPacket = queue.receiveCatching().getOrNull() ?: break

                try {
                    Packet.writePacket(channel, firstPacket)
                    channel.flush()
                } catch (_: ClosedWriteChannelException) {
                    log.debug { "Dropped packet stream — $target channel already closed" }
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
        ioScope.cancel()
    }
}