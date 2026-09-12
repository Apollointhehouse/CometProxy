package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketSetEntityMotion(
    val entityId: Int = 0,
    val motionX: Short = 0,
    val motionY: Short = 0,
    val motionZ: Short = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(entityId)
        sink.writeShort(motionX)
        sink.writeShort(motionY)
        sink.writeShort(motionZ)
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
