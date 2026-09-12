package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source
import kotlinx.io.readFloat

data class PacketEntityFling(
    val entityId: Int = 0,
    val xd: Double = 0.0,
    val yd: Double = 0.0,
    val zd: Double = 0.0,
    val pushTime: Float = 0f,
    val pushesTick: Int = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeFloat(xd.toFloat())
        channel.writeFloat(yd.toFloat())
        channel.writeFloat(zd.toFloat())
        channel.writeFloat(pushTime)
        channel.writeByte(pushesTick.toByte())
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketEntityFling> {
        override val size: Int = 21

        override fun create(buffer: Source): PacketEntityFling {
            val entityId = buffer.readInt()
            val xd = buffer.readFloat().toDouble()
            val yd = buffer.readFloat().toDouble()
            val zd = buffer.readFloat().toDouble()
            val pushTime = buffer.readFloat()
            val pushesTick = buffer.readByte().toUByte().toInt()

            return PacketEntityFling(
                entityId = entityId,
                xd = xd,
                yd = yd,
                zd = zd,
                pushTime = pushTime,
                pushesTick = pushesTick
            )
        }
    }
}
