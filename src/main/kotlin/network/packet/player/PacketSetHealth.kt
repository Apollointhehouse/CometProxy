package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketSetHealth(
    val healthMP: Short = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeShort(healthMP)
    }

    override val estimatedSize: Int
        get() = 2

    companion object : PacketFactory<PacketSetHealth> {
		override suspend fun create(channel: ByteReadChannel): PacketSetHealth {
            val healthMP = channel.readShort()

            return PacketSetHealth(healthMP = healthMP)
        }
    }
}
