package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketSleep(
    val entityID: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    val wtf: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityID)
        channel.writeByte(wtf)
        channel.writeInt(x)
        channel.writeInt(y)
        channel.writeInt(z)
    }

    override val estimatedSize: Int
        get() = 17

    companion object : PacketFactory<PacketSleep> {
		override suspend fun create(channel: ByteReadChannel): PacketSleep {
            val entityID = channel.readInt()
            val wtf = channel.readByte()
            val x = channel.readInt()
            val y = channel.readInt()
            val z = channel.readInt()

            return PacketSleep(entityID = entityID, x = x, y = y, z = z, wtf = wtf)
        }
    }
}
