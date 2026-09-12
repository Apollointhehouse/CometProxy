package dev.apollointhehouse.network.packet.container

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketContainerSetData(
    val windowId: Byte = 0,
    val progressBar: Short = 0,
    val progressBarValue: Short = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeByte(windowId)
        sink.writeShort(progressBar)
        sink.writeShort(progressBarValue)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketContainerSetData> {
        override val size: Int = 5

        override fun create(buffer: Source): PacketContainerSetData {
            val windowId = buffer.readByte()
            val progressBar = buffer.readShort()
            val progressBarValue = buffer.readShort()

            return PacketContainerSetData(
                windowId = windowId,
                progressBar = progressBar,
                progressBarValue = progressBarValue
            )
        }
    }
}
