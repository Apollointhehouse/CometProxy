package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketSetRiding(
    val passengerId: Int = 0,
    val isTileEntity: Boolean = false,
    val vehicleId: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(passengerId)
        channel.writeByte(if (isTileEntity) 1 else 0)
        if (this.isTileEntity) {
            channel.writeInt(x)
            channel.writeInt(y)
            channel.writeInt(z)
        } else {
            channel.writeInt(vehicleId)
        }
    }

    override val estimatedSize: Int
        get() = 8

    companion object : PacketFactory<PacketSetRiding> {
		override val packetID = 39
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