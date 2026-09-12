package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketSetEntityMotion(
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
        get() = size

    companion object : BufferedPacketFactory<PacketSetEntityMotion> {
        override val size: Int = 10

        override fun create(buffer: Source): PacketSetEntityMotion {
            val entityId = buffer.readInt()
            val motionX = buffer.readShort()
            val motionY = buffer.readShort()
            val motionZ = buffer.readShort()

            return PacketSetEntityMotion(entityId = entityId, motionX = motionX, motionY = motionY, motionZ = motionZ)
        }
    }
}
