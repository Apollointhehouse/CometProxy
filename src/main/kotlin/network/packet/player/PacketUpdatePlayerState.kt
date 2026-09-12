package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketUpdatePlayerState(
    val state: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(state)
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