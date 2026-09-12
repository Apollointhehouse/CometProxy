package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.network.packet.BufferedPacketFactory
import dev.apollointhehouse.network.packet.Packet
import kotlinx.io.Sink
import kotlinx.io.Source

data class PacketTakeItemEntity(
    val collectedEntityId: Int = 0,
    val collectorEntityId: Int = 0,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(collectedEntityId)
        sink.writeInt(collectorEntityId)
    }

    override val estimatedSize: Int
        get() = size

    companion object : BufferedPacketFactory<PacketTakeItemEntity> {
        override val size: Int = 8

        override fun create(buffer: Source): PacketTakeItemEntity {
            val collectedEntityId = buffer.readInt()
            val collectorEntityId = buffer.readInt()

            return PacketTakeItemEntity(collectedEntityId = collectedEntityId, collectorEntityId = collectorEntityId)
        }
    }
}
