package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketUpdatePlayerState(
    val state: Byte = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeByte(state)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketUpdatePlayerState> {
        override val size: Int = 1

        override fun create(buffer: Source): PacketUpdatePlayerState {
            val state = buffer.readByte()

            return PacketUpdatePlayerState(state = state)
        }
    }
}