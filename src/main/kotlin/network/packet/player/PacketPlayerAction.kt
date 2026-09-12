package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source
import kotlinx.io.readDouble

data class PacketPlayerAction(
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
        get() = size

    companion object : BufferedPacketFactory<PacketPlayerAction> {
        override val size: Int = 27

        override fun create(buffer: Source): PacketPlayerAction {
            val action = buffer.readByte().toUByte().toInt()
            val xPosition = buffer.readInt()
            val yPosition = buffer.readByte().toUByte().toInt()
            val zPosition = buffer.readInt()
            val side = buffer.readByte().toUByte().toInt()
            val xHit = buffer.readDouble()
            val yHit = buffer.readDouble()

            return PacketPlayerAction(
                xPosition = xPosition,
                yPosition = yPosition,
                zPosition = zPosition,
                xHit = xHit,
                yHit = yHit,
                action = action,
                side = side
            )
        }
    }
}
