package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketUpdatePlayerState(
    val state: Byte = 0,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(state)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : PacketFactory<PacketUpdatePlayerState> {
		override val packetID = 19
        override suspend fun create(channel: ByteReadChannel): PacketUpdatePlayerState {
            val state = channel.readByte()

            return PacketUpdatePlayerState(state = state)
        }
    }
}