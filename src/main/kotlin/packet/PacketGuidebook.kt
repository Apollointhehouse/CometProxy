package dev.apollointhehouse.packet

import dev.apollointhehouse.packet.Packet.Companion.readBoolean
import dev.apollointhehouse.packet.Packet.Companion.writeBoolean
import io.ktor.utils.io.*

class PacketGuidebook(
    val isGuidebookOpen: Boolean = false,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeBoolean(isGuidebookOpen)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : PacketFactory<PacketGuidebook> {
		override val packetID = 133
        override suspend fun create(channel: ByteReadChannel): PacketGuidebook {
            val isGuidebookOpen = channel.readBoolean()

            return PacketGuidebook(isGuidebookOpen = isGuidebookOpen)
        }
    }
}
