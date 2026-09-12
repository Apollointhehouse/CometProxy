package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.*

data class PacketVehicleControl(
    var entityId: Int = 0,
    var targetXD: Double = 0.0,
    var targetYD: Double = 0.0,
    var targetZD: Double = 0.0,
    var targetYRot: Float = 0f,
    var fallDistance: Float = 0f
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(entityId)
        sink.writeDouble(targetXD)
        sink.writeDouble(targetYD)
        sink.writeDouble(targetZD)
        sink.writeFloat(targetYRot)
        sink.writeFloat(fallDistance)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketVehicleControl> {
        override val size: Int = 36

        override fun create(buffer: Source): PacketVehicleControl {
            val entityId = buffer.readInt()
            val targetXD = buffer.readDouble()
            val targetYD = buffer.readDouble()
            val targetZD = buffer.readDouble()
            val targetYRot = buffer.readFloat()
            val fallDistance = buffer.readFloat()

            return PacketVehicleControl(entityId, targetXD, targetYD, targetZD, targetYRot, fallDistance)
        }

    }
}