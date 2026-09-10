package dev.apollointhehouse.network.packet.misc

import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketCustomPayload(
    val netChannel: String = "",
    val data: ByteArray = byteArrayOf(),
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeJavaStringUTF8(netChannel)
        if (data.isNotEmpty()) {
            channel.writeInt(data.size)
            channel.writeFully(data)
        } else {
            channel.writeInt(0)
        }
    }

    override val estimatedSize: Int
        get() = netChannel.length + 4 + data.size

    companion object : PacketFactory<PacketCustomPayload> {
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