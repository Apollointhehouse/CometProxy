package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

data class PacketSetPaintingArt(
    val motive: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(motive)
    }

    override val estimatedSize: Int
        get() = 0

    companion object : PacketFactory<PacketSetPaintingArt> {
        override suspend fun create(channel: ByteReadChannel): PacketSetPaintingArt {
            val motive = channel.readInt()

            return PacketSetPaintingArt(motive = motive)
        }
    }
}
