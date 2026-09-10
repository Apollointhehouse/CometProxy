package dev.apollointhehouse.network.packet.player

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.PacketFactory
import io.ktor.utils.io.*

data class PacketSetEquippedItem(
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
