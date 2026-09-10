package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.extensions.readBoolean
import dev.apollointhehouse.network.extensions.writeBoolean
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketPhotoMode(
    val disabled: Boolean = false,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeBoolean(disabled)
    }

    override val estimatedSize: Int
        get() = 1

    companion object : PacketFactory<PacketPhotoMode> {
		override suspend fun create(channel: ByteReadChannel): PacketPhotoMode {
            val disabled = channel.readBoolean()

            return PacketPhotoMode(disabled = disabled)
        }
    }
}