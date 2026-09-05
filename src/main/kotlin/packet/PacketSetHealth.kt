package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketSetHealth(
    val healthMP: Short = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeShort(healthMP)
    }

    override val estimatedSize: Int
        get() = 2

    companion object : PacketFactory<PacketSetHealth> {
		override val packetID = 8
        override suspend fun create(channel: ByteReadChannel): PacketSetHealth {
            val healthMP = channel.readShort()

            return PacketSetHealth(healthMP = healthMP)
        }
    }
}
