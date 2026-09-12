package dev.apollointhehouse.network.packet.container


import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketContainerClose(
    val windowId: Byte = 0,
) : Packet {


    override fun write(sink: Sink) {
        sink.writeByte(windowId)
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
