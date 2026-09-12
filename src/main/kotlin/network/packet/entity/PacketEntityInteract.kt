package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketEntityInteract(
    val sourceEntityID: Int = 0,
    val targetEntityID: Int = 0,
    val action: Byte = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(sourceEntityID)
        sink.writeInt(targetEntityID)
        sink.writeByte(action)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketEntityInteract> {
        override val size: Int = 9

        override fun create(buffer: Source): PacketEntityInteract {
            val sourceEntityID = buffer.readInt()
            val targetEntityID = buffer.readInt()
            val action = buffer.readByte()

            return PacketEntityInteract(
                sourceEntityID = sourceEntityID,
                targetEntityID = targetEntityID,
                action = action
            )
        }
    }
}
