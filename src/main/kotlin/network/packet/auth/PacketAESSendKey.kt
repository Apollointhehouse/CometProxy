package dev.apollointhehouse.network.packet.auth

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketAESSendKey(
    var key: String,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(key)
    }

    override val estimatedSize: Int
        get() = 128

    companion object : StreamingPacketFactory<PacketAESSendKey> {
		override suspend fun create(channel: ByteReadChannel): PacketAESSendKey {
            val key = channel.readJavaStringUTF8(392)

            return PacketAESSendKey(key = key)
        }
    }
}
