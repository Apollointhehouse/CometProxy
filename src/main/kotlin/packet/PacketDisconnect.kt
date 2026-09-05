package dev.apollointhehouse.packet

import dev.apollointhehouse.packet.Packet.Companion.readJavaStringUTF16BE
import dev.apollointhehouse.packet.Packet.Companion.writeJavaStringUTF16BE
import io.ktor.utils.io.*

class PacketDisconnect(
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
		override val packetID = 255
        override suspend fun create(channel: ByteReadChannel): PacketDisconnect {
            val reason = channel.readJavaStringUTF16BE(255)

            return PacketDisconnect(reason = reason)
        }
    }
}