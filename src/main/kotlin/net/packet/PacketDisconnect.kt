package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readJavaStringUTF16BE
import dev.apollointhehouse.utils.extensions.writeJavaStringUTF16BE
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