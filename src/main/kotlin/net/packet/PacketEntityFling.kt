package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketEntityFling(
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
        get() = 21

    companion object : PacketFactory<PacketEntityFling> {
		override suspend fun create(channel: ByteReadChannel): PacketEntityFling {
            val entityId = channel.readInt()
            val xd = channel.readFloat().toDouble()
            val yd = channel.readFloat().toDouble()
            val zd = channel.readFloat().toDouble()
            val pushTime = channel.readFloat()
            val pushesTick = channel.readByte().toUByte().toInt()

            return PacketEntityFling(entityId = entityId, xd = xd, yd = yd, zd = zd, pushTime = pushTime, pushesTick = pushesTick)
        }
    }
}
