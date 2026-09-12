package dev.apollointhehouse.network.packet.auth

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketAESSendKey(
    var key: String,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeJavaStringUTF8(key)
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
