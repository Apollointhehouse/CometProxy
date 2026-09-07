package dev.apollointhehouse.net.packet

import dev.apollointhehouse.data.SyncedEntityData
import io.ktor.utils.io.*

class PacketSetEntityData(
    val entityId: Int = 0,
    val packedItems: List<SyncedEntityData.DataItem<*>> = listOf(),
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        SyncedEntityData.pack(packedItems, channel)
    }

    override val estimatedSize: Int
        get() = 5

    companion object : PacketFactory<PacketSetEntityData> {
		override suspend fun create(channel: ByteReadChannel): PacketSetEntityData {
            val entityId = channel.readInt()
            val packedItems = SyncedEntityData.unpack(channel)

            return PacketSetEntityData(entityId = entityId, packedItems = packedItems)
        }
    }
}
