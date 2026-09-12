package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source
import kotlinx.io.readFloat
import kotlinx.io.writeFloat

data class PacketEntityFling(
    val entityId: Int = 0,
    val xd: Double = 0.0,
    val yd: Double = 0.0,
    val zd: Double = 0.0,
    val pushTime: Float = 0f,
    val pushesTick: Int = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(entityId)
        sink.writeFloat(xd.toFloat())
        sink.writeFloat(yd.toFloat())
        sink.writeFloat(zd.toFloat())
        sink.writeFloat(pushTime)
        sink.writeByte(pushesTick.toByte())
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
