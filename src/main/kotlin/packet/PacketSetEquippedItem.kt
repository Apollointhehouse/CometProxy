package dev.apollointhehouse.packet

import dev.apollointhehouse.data.nbt.tags.CompoundTag
import dev.apollointhehouse.packet.Packet.Companion.readCompressedCompoundTag
import dev.apollointhehouse.packet.Packet.Companion.writeCompressedCompoundTag
import io.ktor.utils.io.*

class PacketSetEquippedItem(
    val entityID: Int = 0,
    val slot: Short = 0,
    val itemID: Short = 0,
    val itemMeta: Short = 0,
    val itemData: CompoundTag? = null,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityID)
        channel.writeShort(slot)
        channel.writeShort(itemID)
        channel.writeShort(itemMeta)
        channel.writeCompressedCompoundTag(itemData)
    }

    override val estimatedSize: Int
        get() = 8

    companion object : PacketFactory<PacketSetEquippedItem> {
		override val packetID = 5
        override suspend fun create(channel: ByteReadChannel): PacketSetEquippedItem {
            val entityID = channel.readInt()
            val slot = channel.readShort()
            val itemID = channel.readShort()
            val itemMeta = channel.readShort()
            val itemData = channel.readCompressedCompoundTag()

            return PacketSetEquippedItem(entityID = entityID, slot = slot, itemID = itemID, itemMeta = itemMeta, itemData = itemData)
        }
    }
}
