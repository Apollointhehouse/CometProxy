package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketTeleportEntity(
    val id: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    val yaw: Int = 0,
    val pitch: Int = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(id)
        sink.writeInt(x)
        sink.writeInt(y)
        sink.writeInt(z)
        sink.writeByte(yaw.toByte())
        sink.writeByte(pitch.toByte())
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketTeleportEntity> {
        override val size: Int = 18

        override fun create(buffer: Source): PacketTeleportEntity {
            val id = buffer.readInt()
            val x = buffer.readInt()
            val y = buffer.readInt()
            val z = buffer.readInt()
            val yaw = buffer.readByte().toUByte().toInt()
            val pitch = buffer.readByte().toUByte().toInt()

            return PacketTeleportEntity(id = id, x = x, y = y, z = z, yaw = yaw, pitch = pitch)
        }
    }
}
