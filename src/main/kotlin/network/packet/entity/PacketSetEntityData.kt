package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.model.EntityDataItem
import dev.apollointhehouse.model.SyncedEntityData
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketSetEntityData(
    val entityId: Int = 0,
    val packedItems: List<EntityDataItem<*>> = listOf(),
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(entityId)
        SyncedEntityData.pack(packedItems, sink)
    }

    override val estimatedSize: Int
        get() = 5

    companion object : StreamingPacketFactory<PacketSetEntityData> {
        override suspend fun create(channel: ByteReadChannel): PacketSetEntityData {
            val entityId = channel.readInt()
            val packedItems = SyncedEntityData.unpack(channel)

            return PacketSetEntityData(entityId = entityId, packedItems = packedItems)
        }
    }
}
