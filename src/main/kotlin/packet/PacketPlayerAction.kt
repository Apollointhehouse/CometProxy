package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketPlayerAction(
    val xPosition: Int = 0,
    val yPosition: Int = 0,
    val zPosition: Int = 0,
    val xHit: Double = 0.0,
    val yHit: Double = 0.0,
    val action: Int = 0,
    val side: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(action.toByte())
        channel.writeInt(xPosition)
        channel.writeByte(yPosition.toByte())
        channel.writeInt(zPosition)
        channel.writeByte(side.toByte())
        channel.writeDouble(xHit)
        channel.writeDouble(yHit)
    }

    override val estimatedSize: Int
        get() = 19

    companion object : PacketFactory<PacketPlayerAction> {
		override val packetID = 14
        override suspend fun create(channel: ByteReadChannel): PacketPlayerAction {
            val action = channel.readByte().toUByte().toInt()
            val xPosition = channel.readInt()
            val yPosition = channel.readByte().toUByte().toInt()
            val zPosition = channel.readInt()
            val side = channel.readByte().toUByte().toInt()
            val xHit = channel.readDouble()
            val yHit = channel.readDouble()

            return PacketPlayerAction(xPosition = xPosition, yPosition = yPosition, zPosition = zPosition, xHit = xHit, yHit = yHit, action = action, side = side)
        }
    }
}
