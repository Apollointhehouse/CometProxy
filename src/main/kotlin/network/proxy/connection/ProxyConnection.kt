package dev.apollointhehouse.network.proxy.connection

import dev.apollointhehouse.network.packet.Packet
import io.ktor.network.sockets.*
import io.ktor.utils.io.*

class ProxyConnection(
    val socket: Socket,
    val input: ByteReadChannel,
    val output: ByteWriteChannel
) : AutoCloseable {
    suspend fun writePacket(packet: Packet) {
        Packet.writePacket(output, packet)
    }

    suspend fun readPacket(): Packet? =
        Packet.readPacket(input)

    override fun close() {
        socket.close()
    }
}

fun Connection.toProxyConnection(): ProxyConnection =
    ProxyConnection(socket, input, output)