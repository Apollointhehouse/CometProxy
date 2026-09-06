package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketFlagOpen(
    val windowId: Byte = 0,
    val x: Int = 0,
    val y: Short = 0,
    val z: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(windowId)
        channel.writeInt(x)
        channel.writeShort(y)
        channel.writeInt(z)
    }

    override val estimatedSize: Int
        get() = 11

    companion object : PacketFactory<PacketFlagOpen> {
		override val packetID = 142
        override suspend fun create(channel: ByteReadChannel): PacketFlagOpen {
            val windowId = channel.readByte()
            val x = channel.readInt()
            val y = channel.readShort()
            val z = channel.readInt()

            return PacketFlagOpen(windowId = windowId, x = x, y = y, z = z)
        }
    }
}
