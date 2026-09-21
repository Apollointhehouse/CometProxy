package dev.apollointhehouse.network.proxy.connection

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketSink
import dev.apollointhehouse.network.packet.PacketSource
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
    private val socket: Socket,
    private val input: ByteReadChannel,
    private val output: ByteWriteChannel
) : AutoCloseable, PacketSource, PacketSink {
    private val writerQueue = Channel<Packet>(Channel.UNLIMITED)
    private val ioScope = CoroutineScope(Dispatchers.Default)
    private val log = logger()

    init {
        ioScope.launch { writerLoop() }
    }

    suspend fun writePacket(packet: Packet) {
        Packet.writePacket(output, packet)
    }

    override fun sendPacket(packet: Packet) {
        writerQueue.trySend(packet).onFailure {
            log.warn { "Failed to enqueue ${packet::class.simpleName}" }
        }
    }

    override suspend fun receivePacket(): Packet? =
        Packet.readPacket(input)

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

    override fun toString(): String =
        socket.remoteAddress.toString()
}

fun Connection.toProxyConnection(): ProxyConnection =
    ProxyConnection(socket, input, output)