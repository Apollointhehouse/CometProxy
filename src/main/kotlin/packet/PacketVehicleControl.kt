package dev.apollointhehouse.packet

import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.ByteWriteChannel
import io.ktor.utils.io.readDouble
import io.ktor.utils.io.readFloat
import io.ktor.utils.io.readInt
import io.ktor.utils.io.writeDouble
import io.ktor.utils.io.writeFloat
import io.ktor.utils.io.writeInt

class PacketVehicleControl(
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
        get() = 36

    companion object : PacketFactory<PacketVehicleControl> {
        override val packetID = 26

        override suspend fun create(channel: ByteReadChannel): PacketVehicleControl {
            val entityId = channel.readInt()
            val targetXD = channel.readDouble()
            val targetYD = channel.readDouble()
            val targetZD = channel.readDouble()
            val targetYRot = channel.readFloat()
            val fallDistance = channel.readFloat()

            return PacketVehicleControl(entityId, targetXD, targetYD, targetZD, targetYRot, fallDistance)
        }

    }
}