package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketSetCarriedItem(
    val id: Short = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeShort(id)
    }

    override val estimatedSize: Int
        get() = 2

    companion object : PacketFactory<PacketSetCarriedItem> {
		override val packetID = 16
        override suspend fun create(channel: ByteReadChannel): PacketSetCarriedItem {
            val id = channel.readShort()

            return PacketSetCarriedItem(id = id)
        }
    }
}
