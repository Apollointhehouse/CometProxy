package dev.apollointhehouse.network.packet.world

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketBlockUpdate(
    val xPosition: Int = 0,
    val yPosition: Short = 0,
    val zPosition: Int = 0,
    val blockId: Short = 0,
    val metadata: Int = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(xPosition)
        sink.writeShort(yPosition)
        sink.writeInt(zPosition)
        sink.writeShort(blockId)
        sink.writeByte(metadata.toByte())
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketBlockUpdate> {
        override val size: Int = 13

        override fun create(buffer: Source): PacketBlockUpdate {
            val xPosition = buffer.readInt()
            val yPosition = buffer.readShort()
            val zPosition = buffer.readInt()
            val blockId = buffer.readShort()
            val metadata = buffer.readByte().toUByte().toInt()

            return PacketBlockUpdate(
                xPosition = xPosition,
                yPosition = yPosition,
                zPosition = zPosition,
                blockId = blockId,
                metadata = metadata
            )
        }
    }
}
