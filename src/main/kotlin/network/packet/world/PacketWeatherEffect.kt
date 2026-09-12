package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketWeatherEffect(
    val id: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    val effectId: Byte = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(id)
        sink.writeByte(effectId)
        sink.writeInt(x)
        sink.writeInt(y)
        sink.writeInt(z)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketWeatherEffect> {
        override val size: Int = 17

        override fun create(buffer: Source): PacketWeatherEffect {
            val id = buffer.readInt()
            val effectId = buffer.readByte()
            val x = buffer.readInt()
            val y = buffer.readInt()
            val z = buffer.readInt()

            return PacketWeatherEffect(id = id, x = x, y = y, z = z, effectId = effectId)
        }
    }
}
