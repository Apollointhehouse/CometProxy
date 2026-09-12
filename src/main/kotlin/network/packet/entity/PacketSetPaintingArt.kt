package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketSetPaintingArt(
    val motive: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(motive)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketSetPaintingArt> {
        override val size: Int = 4

        override fun create(buffer: Source): PacketSetPaintingArt {
            val motive = buffer.readInt()

            return PacketSetPaintingArt(motive = motive)
        }
    }
}
