package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketSetHotbarOffset(
    val hotbarOffset: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(hotbarOffset)
    }

    override val estimatedSize: Int
        get() = 0

    companion object : PacketFactory<PacketSetHotbarOffset> {
		override val packetID = 108
        override suspend fun create(channel: ByteReadChannel): PacketSetHotbarOffset {
            val hotbarOffset = channel.readByte()

            return PacketSetHotbarOffset(hotbarOffset = hotbarOffset)
        }
    }
}
