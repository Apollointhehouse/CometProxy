package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

data class PacketSetHotbarOffset(
    val hotbarOffset: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(hotbarOffset)
    }

    override val estimatedSize: Int
        get() = 0

    companion object : PacketFactory<PacketSetHotbarOffset> {
		override suspend fun create(channel: ByteReadChannel): PacketSetHotbarOffset {
            val hotbarOffset = channel.readByte()

            return PacketSetHotbarOffset(hotbarOffset = hotbarOffset)
        }
    }
}
