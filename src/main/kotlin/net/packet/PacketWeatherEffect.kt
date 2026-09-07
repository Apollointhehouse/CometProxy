package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketWeatherEffect(
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
        get() = 17

    companion object : PacketFactory<PacketWeatherEffect> {
		override suspend fun create(channel: ByteReadChannel): PacketWeatherEffect {
            val id = channel.readInt()
            val effectId = channel.readByte()
            val x = channel.readInt()
            val y = channel.readInt()
            val z = channel.readInt()

            return PacketWeatherEffect(id = id, x = x, y = y, z = z, effectId = effectId)
        }
    }
}
