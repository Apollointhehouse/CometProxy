package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source
import kotlinx.io.readDouble
import kotlinx.io.readFloat

data class PacketVehicleControl(
    var entityId: Int = 0,
    var targetXD: Double = 0.0,
    var targetYD: Double = 0.0,
    var targetZD: Double = 0.0,
    var targetYRot: Float = 0f,
    var fallDistance: Float = 0f
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeDouble(targetXD)
        channel.writeDouble(targetYD)
        channel.writeDouble(targetZD)
        channel.writeFloat(targetYRot)
        channel.writeFloat(fallDistance)
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