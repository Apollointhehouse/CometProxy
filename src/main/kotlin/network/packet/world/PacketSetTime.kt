package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketSetTime(
    val time: Long = 0L,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeLong(time)
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
