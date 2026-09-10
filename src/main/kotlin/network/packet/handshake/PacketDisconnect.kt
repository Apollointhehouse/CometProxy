package dev.apollointhehouse.network.packet.handshake

import dev.apollointhehouse.network.extensions.readJavaStringUTF16BE
import dev.apollointhehouse.network.extensions.writeJavaStringUTF16BE
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketDisconnect(
    val image: ByteArray = byteArrayOf(),
    var reason: String = "",
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF16BE(reason)
        channel.writeFully(image)
    }

    override val estimatedSize: Int
        get() = reason.length

    companion object : PacketFactory<PacketDisconnect> {
		override suspend fun create(channel: ByteReadChannel): PacketDisconnect {
            val reason = channel.readJavaStringUTF16BE(255)

            return PacketDisconnect(reason = reason)
        }
    }
}