package dev.apollointhehouse.packet

import dev.apollointhehouse.packet.Packet.Companion.readJavaStringUTF8
import dev.apollointhehouse.packet.Packet.Companion.writeJavaStringUTF8
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
		override val packetID = 136
        override suspend fun create(channel: ByteReadChannel): PacketAESSendKey {
            val key = channel.readJavaStringUTF8(392)

            return PacketAESSendKey(key = key)
        }
    }
}
