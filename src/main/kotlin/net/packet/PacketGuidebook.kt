package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readBoolean
import dev.apollointhehouse.utils.extensions.writeBoolean
import io.ktor.utils.io.*

data class PacketGuidebook(
    val isGuidebookOpen: Boolean = false,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeBoolean(isGuidebookOpen)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : PacketFactory<PacketGuidebook> {
		override suspend fun create(channel: ByteReadChannel): PacketGuidebook {
            val isGuidebookOpen = channel.readBoolean()

            return PacketGuidebook(isGuidebookOpen = isGuidebookOpen)
        }
    }
}
