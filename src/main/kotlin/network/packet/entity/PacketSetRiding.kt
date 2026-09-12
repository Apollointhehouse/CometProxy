package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketSetRiding(
    val passengerId: Int = 0,
    val isTileEntity: Boolean = false,
    val vehicleId: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(passengerId)
        sink.writeByte(if (isTileEntity) 1 else 0)
        if (this.isTileEntity) {
            sink.writeInt(x)
            sink.writeInt(y)
            sink.writeInt(z)
        } else {
            sink.writeInt(vehicleId)
        }
    }

    override val estimatedSize: Int
        get() = 8

    companion object : StreamingPacketFactory<PacketSetRiding> {
        override suspend fun create(channel: ByteReadChannel): PacketSetRiding {
            val passengerId = channel.readInt()
            val isTileEntity = channel.readByte().toInt() != 0

            var vehicleId = 0
            var x = 0
            var y = 0
            var z = 0
            if (isTileEntity) {
                x = channel.readInt()
                y = channel.readInt()
                z = channel.readInt()
            } else {
                vehicleId = channel.readInt()
            }

            return PacketSetRiding(
                passengerId = passengerId,
                isTileEntity = isTileEntity,
                vehicleId = vehicleId,
                x = x,
                y = y,
                z = z
            )
        }
    }
}