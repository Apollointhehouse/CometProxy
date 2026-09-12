package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketAnimate(
    val entityId: Int = 0,
    val animate: Byte = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(entityId)
        sink.writeByte(animate)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketAnimate> {
        override val size: Int = 5

        override fun create(buffer: Source): PacketAnimate {
            val entityId = buffer.readInt()
            val animate = buffer.readByte()

            return PacketAnimate(entityId = entityId, animate = animate)
        }
    }
}