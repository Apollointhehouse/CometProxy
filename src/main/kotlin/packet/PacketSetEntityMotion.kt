package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketSetEntityMotion(
    val entityId: Int = 0,
    val motionX: Short = 0,
    val motionY: Short = 0,
    val motionZ: Short = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeShort(motionX)
        channel.writeShort(motionY)
        channel.writeShort(motionZ)
    }

    override val estimatedSize: Int
        get() = 10

    companion object : PacketFactory<PacketSetEntityMotion> {
		override val packetID = 28
        override suspend fun create(channel: ByteReadChannel): PacketSetEntityMotion {
            val entityId = channel.readInt()
            val motionX = channel.readShort()
            val motionY = channel.readShort()
            val motionZ = channel.readShort()

            return PacketSetEntityMotion(entityId = entityId, motionX = motionX, motionY = motionY, motionZ = motionZ)
        }
    }
}
