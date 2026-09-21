package dev.apollointhehouse.network.proxy.connection

import dev.apollointhehouse.network.packet.Packet
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.onFailure
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.io.IOException
import org.apache.logging.log4j.kotlin.logger

class ProxyConnection(
    val socket: Socket,
    val input: ByteReadChannel,
    val output: ByteWriteChannel
) : AutoCloseable {
    private val writerQueue = Channel<Packet>(Channel.UNLIMITED)
    private val ioScope = CoroutineScope(Dispatchers.Default)
    private val log = logger()

    init {
        ioScope.launch { writerLoop() }
    }

    suspend fun writePacket(packet: Packet) {
        Packet.writePacket(output, packet)
    }

    suspend fun readPacket(): Packet? =
        Packet.readPacket(input)

    fun queuePacket(packet: Packet) {
        writerQueue.trySend(packet).onFailure {
            log.warn { "Failed to enqueue ${packet::class.simpleName}" }
        }
    }

    private suspend fun writerLoop() = use {
        while (ioScope.isActive) {
            val packet = writerQueue.receiveCatching().getOrNull() ?: break

            try {
                writePacket(packet)
            } catch (_: ClosedWriteChannelException) {
                log.debug { "Dropped packet stream: channel already closed" }
                break
            } catch (e: IOException) {
                log.warn(e) { "Failed to write packet" }
            }
        }
    }

    override fun close() {
        socket.close()
    }
}

fun Connection.toProxyConnection(): ProxyConnection =
    ProxyConnection(socket, input, output)