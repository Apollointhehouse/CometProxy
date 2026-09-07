package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readJavaStringUTF8
import dev.apollointhehouse.utils.extensions.writeJavaStringUTF8
import io.ktor.utils.io.*

class PacketAESSendKey(
    var key: String,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(key)
    }

    override val estimatedSize: Int
        get() = 128

    companion object : PacketFactory<PacketAESSendKey> {
		override suspend fun create(channel: ByteReadChannel): PacketAESSendKey {
            val key = channel.readJavaStringUTF8(392)

            return PacketAESSendKey(key = key)
        }
    }
}
