package dev.apollointhehouse.packet

import io.ktor.utils.io.*

class PacketSetTime(
    val time: Long = 0L,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeLong(time)
    }

    override val estimatedSize: Int
        get() = 8

    companion object : PacketFactory<PacketSetTime> {
		override val packetID = 4
        override suspend fun create(channel: ByteReadChannel): PacketSetTime {
            val time = channel.readLong()

            return PacketSetTime(time = time)
        }
    }
}
