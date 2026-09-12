package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketContainerAck(
    val windowId: Byte = 0,
    val shortWindowId: Short = 0,
    val accepted: Boolean = false,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeByte(windowId)
        sink.writeShort(shortWindowId)
        sink.writeByte(if (accepted) 1 else 0)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketContainerAck> {
        override val size: Int = 4

        override fun create(buffer: Source): PacketContainerAck {
            val windowId = buffer.readByte()
            val shortWindowId = buffer.readShort()
            val accepted = buffer.readByte().toInt() != 0

            return PacketContainerAck(windowId = windowId, shortWindowId = shortWindowId, accepted = accepted)
        }
    }
}
