package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.network.extensions.readBoolean
import dev.apollointhehouse.network.extensions.writeBoolean
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketGuidebook(
    val isGuidebookOpen: Boolean = false,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeBoolean(isGuidebookOpen)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : StreamingPacketFactory<PacketGuidebook> {
        override suspend fun create(channel: ByteReadChannel): PacketGuidebook {
            val isGuidebookOpen = channel.readBoolean()

            return PacketGuidebook(isGuidebookOpen = isGuidebookOpen)
        }
    }
}
