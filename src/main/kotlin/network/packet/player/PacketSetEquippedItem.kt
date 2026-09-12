package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*
import kotlinx.io.Sink

data class PacketSetEquippedItem(
    val entityID: Int = 0,
    val slot: Short = 0,
    val itemID: Short = 0,
    val itemMeta: Short = 0,
    val itemData: CompoundTag? = null,
) : Packet {
    override fun write(sink: Sink) {
        sink.writeInt(entityID)
        sink.writeShort(slot)
        sink.writeShort(itemID)
        sink.writeShort(itemMeta)
        sink.writeCompressedCompoundTag(itemData)
    }

    override val estimatedSize: Int
        get() = 8

    companion object : StreamingPacketFactory<PacketSetEquippedItem> {
        override suspend fun create(channel: ByteReadChannel): PacketSetEquippedItem {
            val entityID = channel.readInt()
            val slot = channel.readShort()
            val itemID = channel.readShort()
            val itemMeta = channel.readShort()
            val itemData = channel.readCompressedCompoundTag()

            return PacketSetEquippedItem(
                entityID = entityID,
                slot = slot,
                itemID = itemID,
                itemMeta = itemMeta,
                itemData = itemData
            )
        }
    }
}
