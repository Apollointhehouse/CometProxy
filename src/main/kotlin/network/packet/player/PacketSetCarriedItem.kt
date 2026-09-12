package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketSetCarriedItem(
    val id: Short = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeShort(id)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketSetCarriedItem> {
        override val size: Int = 2

        override fun create(buffer: Source): PacketSetCarriedItem {
            val id = buffer.readShort()

            return PacketSetCarriedItem(id = id)
        }
    }
}
