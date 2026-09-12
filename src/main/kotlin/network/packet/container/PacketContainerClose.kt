package dev.apollointhehouse.network.packet.container


import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import io.ktor.utils.io.*
import kotlinx.io.Source

data class PacketContainerClose(
    val windowId: Byte = 0,
) : Packet {


    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(windowId)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketContainerClose> {
        override val size: Int = 1

        override fun create(buffer: Source): PacketContainerClose {
            val windowId = buffer.readByte()

            return PacketContainerClose(windowId = windowId)
        }
    }
}
