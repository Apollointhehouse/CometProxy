package dev.apollointhehouse.network.packet.handshake

import dev.apollointhehouse.network.extensions.readJavaStringUTF16BE
import dev.apollointhehouse.network.extensions.writeJavaStringUTF16BE
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.io.Sink

data class PacketDisconnect(
    val image: ByteArray = byteArrayOf(),
    var reason: String = "",
) : Packet {
    override fun write(sink: Sink) {
        sink.writeJavaStringUTF16BE(reason)
        sink.writeFully(image)
    }

    override val estimatedSize: Int
        get() = reason.length

    companion object : StreamingPacketFactory<PacketDisconnect> {
        override suspend fun create(channel: ByteReadChannel): PacketDisconnect {
            val reason = channel.readJavaStringUTF16BE(255)

            return PacketDisconnect(reason = reason)
        }
    }
}