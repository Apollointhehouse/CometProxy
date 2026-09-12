package dev.apollointhehouse.network.packet.handshake

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketServerIcon(
    val image: ByteArray = byteArrayOf(),
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(image.size)
        channel.writeFully(image)
    }

    override val estimatedSize: Int
        get() = this.image.size + 4

    companion object : StreamingPacketFactory<PacketServerIcon> {
		override suspend fun create(channel: ByteReadChannel): PacketServerIcon {
            val size = channel.readInt()
            val image = ByteArray(size)

            channel.readFully(image)

            return PacketServerIcon(image = image)
        }
    }
}