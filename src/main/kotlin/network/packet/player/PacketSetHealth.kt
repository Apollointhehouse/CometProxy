package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketSetHealth(
    val healthMP: Short = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeShort(healthMP)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketSetHealth> {
        override val size: Int = 2

        override fun create(buffer: Source): PacketSetHealth {
            val healthMP = buffer.readShort()

            return PacketSetHealth(healthMP = healthMP)
        }
    }
}
