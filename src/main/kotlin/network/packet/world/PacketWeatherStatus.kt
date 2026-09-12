package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source
import kotlinx.io.readFloat

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
        get() = size

    companion object : BufferedPacketFactory<PacketWeatherStatus> {
        override val size: Int = 28

        override fun create(buffer: Source): PacketWeatherStatus {
            val dimId = buffer.readInt()
            val id = buffer.readInt()
            val newId = buffer.readInt()
            val duration = buffer.readLong()
            val intensity = buffer.readFloat()
            val power = buffer.readFloat()

            return PacketWeatherStatus(
                dimId = dimId,
                id = id,
                newId = newId,
                duration = duration,
                intensity = intensity,
                power = power
            )
        }
    }
}
