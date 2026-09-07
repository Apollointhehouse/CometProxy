package dev.apollointhehouse.net.packet

import dev.apollointhehouse.utils.extensions.readBoolean
import dev.apollointhehouse.utils.extensions.writeBoolean
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
		override suspend fun create(channel: ByteReadChannel): PacketPhotoMode {
            val disabled = channel.readBoolean()

            return PacketPhotoMode(disabled = disabled)
        }
    }
}