package dev.apollointhehouse.network.packet.handshake

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import io.ktor.utils.io.core.*
import kotlinx.io.Sink

data class PacketServerIcon(
    val image: ByteArray = byteArrayOf(),
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(image.size)
        sink.writeFully(image)
    }

    override val estimatedSize: Int
        get() = this.image.size + 4

    companion object : StreamingPacketFactory<PacketServerIcon> {
        override suspend fun create(channel: ByteReadChannel): PacketServerIcon {
            val size = channel.readInt()
            val image = ByteArray(size)

            channel.readFully(image)

            return PacketServerIcon(image = image)
        }
    }
}