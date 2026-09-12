package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketFlagOpen(
    val windowId: Byte = 0,
    val x: Int = 0,
    val y: Short = 0,
    val z: Int = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeByte(windowId)
        sink.writeInt(x)
        sink.writeShort(y)
        sink.writeInt(z)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketFlagOpen> {
        override val size: Int = 11

        override fun create(buffer: Source): PacketFlagOpen {
            val windowId = buffer.readByte()
            val x = buffer.readInt()
            val y = buffer.readShort()
            val z = buffer.readInt()

            return PacketFlagOpen(windowId = windowId, x = x, y = y, z = z)
        }
    }
}
