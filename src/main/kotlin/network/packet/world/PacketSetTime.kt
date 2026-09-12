package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketSetTime(
    val time: Long = 0L,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeLong(time)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketSetTime> {
        override val size: Int = 8

        override fun create(buffer: Source): PacketSetTime {
            val time = buffer.readLong()

            return PacketSetTime(time = time)
        }
    }
}
