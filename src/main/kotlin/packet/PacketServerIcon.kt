package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketServerIcon(
    val image: ByteArray = byteArrayOf(),
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(image.size)
        channel.writeFully(image)
    }

    override val estimatedSize: Int
        get() = this.image.size + 4

    companion object : PacketFactory<PacketServerIcon> {
		override val packetID = 253
        override suspend fun create(channel: ByteReadChannel): PacketServerIcon {
            val size = channel.readInt()
            val image = ByteArray(size)

            channel.readFully(image)

            return PacketServerIcon(image = image)
        }
    }
}