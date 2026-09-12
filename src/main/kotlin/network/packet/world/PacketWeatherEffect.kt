package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketWeatherEffect(
    val id: Int = 0,
    val x: Int = 0,
    val y: Int = 0,
    val z: Int = 0,
    val effectId: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(id)
        channel.writeByte(effectId)
        channel.writeInt(x)
        channel.writeInt(y)
        channel.writeInt(z)
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
