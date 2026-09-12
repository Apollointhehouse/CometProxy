package dev.apollointhehouse.network.packet.misc

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.io.Sink

data class PacketCustomPayload(
    val netChannel: String = "",
    val data: ByteArray = byteArrayOf(),
) : Packet {
    override fun write(sink: Sink) {
        sink.writeJavaStringUTF8(netChannel)
        if (data.isNotEmpty()) {
            sink.writeInt(data.size)
            sink.writeFully(data)
        } else {
            sink.writeInt(0)
        }
    }

    override val estimatedSize: Int
        get() = netChannel.length + 4 + data.size

    companion object : StreamingPacketFactory<PacketCustomPayload> {
        override suspend fun create(channel: ByteReadChannel): PacketCustomPayload {
            val netChannel = channel.readJavaStringUTF8(128)
            val length = channel.readInt()
            var data = byteArrayOf()
            if (length in 1..<32768) {
                data = ByteArray(length)
                channel.readFully(data)
            }

            return PacketCustomPayload(netChannel = netChannel, data = data)
        }
    }
}