package dev.apollointhehouse.network.packet.entity

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.StreamingPacketFactory
import io.ktor.utils.io.*

data class PacketAddItemEntity(
    val entityId: Int = 0,
    val xPosition: Int = 0,
    val yPosition: Int = 0,
    val zPosition: Int = 0,
    val xd: Byte = 0,
    val yd: Byte = 0,
    val zd: Byte = 0,
    val itemID: Short = 0,
    val count: Byte = 0,
    val itemDamage: Short = 0,
    val tag: CompoundTag? = null,
) : Packet {
    override suspend fun write(channel: ByteWriteChannel) {
        channel.writeInt(entityId)
        channel.writeShort(itemID)
        channel.writeByte(count)
        channel.writeShort(itemDamage)
        channel.writeCompressedCompoundTag(this.tag!!)
        channel.writeInt(xPosition)
        channel.writeInt(yPosition)
        channel.writeInt(zPosition)
        channel.writeByte(xd)
        channel.writeByte(yd)
        channel.writeByte(zd)
    }

    override val estimatedSize: Int
        get() = 24

    companion object : StreamingPacketFactory<PacketAddItemEntity> {
		override suspend fun create(channel: ByteReadChannel): PacketAddItemEntity {
            val entityId = channel.readInt()
            val itemID = channel.readShort()
            val count = channel.readByte()
            val itemDamage = channel.readShort()
            val tag = channel.readCompressedCompoundTag()
            val xPosition = channel.readInt()
            val yPosition = channel.readInt()
            val zPosition = channel.readInt()
            val xd = channel.readByte()
            val yd = channel.readByte()
            val zd = channel.readByte()

            return PacketAddItemEntity(entityId = entityId, xPosition = xPosition, yPosition = yPosition, zPosition = zPosition, xd = xd, yd = yd, zd = zd, itemID = itemID, count = count, itemDamage = itemDamage, tag = tag)
        }
    }
}
