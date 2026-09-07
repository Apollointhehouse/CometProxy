package dev.apollointhehouse.net.packet

import io.ktor.utils.io.*

class PacketContainerAck(
    val windowId: Byte = 0,
    val shortWindowId: Short = 0,
    val accepted: Boolean = false,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeByte(windowId)
        channel.writeShort(shortWindowId)
        channel.writeByte(if (accepted) 1 else 0)
    }

    override val estimatedSize: Int
        get() = 4

    companion object : PacketFactory<PacketContainerAck> {
		override suspend fun create(channel: ByteReadChannel): PacketContainerAck {
            val windowId = channel.readByte()
            val shortWindowId = channel.readShort()
            val accepted = channel.readByte().toInt() != 0

            return PacketContainerAck(windowId = windowId, shortWindowId = shortWindowId, accepted = accepted)
        }
    }
}
