package dev.apollointhehouse.packet

import dev.apollointhehouse.packet.Packet.Companion.readBoolean
import dev.apollointhehouse.packet.Packet.Companion.writeBoolean
import io.ktor.utils.io.*

class PacketPhotoMode(
    val disabled: Boolean = false,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeBoolean(disabled)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : PacketFactory<PacketPhotoMode> {
		override val packetID = 143
        override suspend fun create(channel: ByteReadChannel): PacketPhotoMode {
            val disabled = channel.readBoolean()

            return PacketPhotoMode(disabled = disabled)
        }
    }
}