package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.extensions.readBoolean
import dev.apollointhehouse.network.extensions.writeBoolean
import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketPhotoMode(
    val disabled: Boolean = false,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeBoolean(disabled)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketPhotoMode> {
        override val size: Int = 1

        override fun create(buffer: Source): PacketPhotoMode {
            val disabled = buffer.readBoolean()

            return PacketPhotoMode(disabled = disabled)
        }
    }
}