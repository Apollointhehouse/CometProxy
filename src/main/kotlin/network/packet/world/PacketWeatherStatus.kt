package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketWeatherStatus(
    val dimId: Int = 0,
    val id: Int = 0,
    val newId: Int = 0,
    val duration: Long = 0L,
    val intensity: Float = 0f,
    val power: Float = 0f,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(dimId)
        channel.writeInt(id)
        channel.writeInt(newId)
        channel.writeLong(duration)
        channel.writeFloat(intensity)
        channel.writeFloat(power)
    }

    override val estimatedSize: Int
        get() = 28

    companion object : PacketFactory<PacketWeatherStatus> {
		override suspend fun create(channel: ByteReadChannel): PacketWeatherStatus {
            val dimId = channel.readInt()
            val id = channel.readInt()
            val newId = channel.readInt()
            val duration = channel.readLong()
            val intensity = channel.readFloat()
            val power = channel.readFloat()

            return PacketWeatherStatus(dimId = dimId, id = id, newId = newId, duration = duration, intensity = intensity, power = power)
        }
    }
}
