package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketSetHotbarOffset(
    val hotbarOffset: Byte = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeByte(hotbarOffset)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketSetHotbarOffset> {
        override val size: Int = 1

        override fun create(buffer: Source): PacketSetHotbarOffset {
            val hotbarOffset = buffer.readByte()

            return PacketSetHotbarOffset(hotbarOffset = hotbarOffset)
        }
    }
}
