package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketBlockEvent(
    val xLocation: Int = 0,
    val yLocation: Short = 0,
    val zLocation: Int = 0,
    val index: Byte = 0,
    val data: Byte = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(xLocation)
        sink.writeShort(yLocation)
        sink.writeInt(zLocation)
        sink.writeByte(index)
        sink.writeByte(data)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketBlockEvent> {
        override val size: Int = 12

        override fun create(buffer: Source): PacketBlockEvent {
            val xLocation = buffer.readInt()
            val yLocation = buffer.readShort()
            val zLocation = buffer.readInt()
            val index = buffer.readByte()
            val data = buffer.readByte()

            return PacketBlockEvent(
                xLocation = xLocation,
                yLocation = yLocation,
                zLocation = zLocation,
                index = index,
                data = data
            )
        }
    }
}
