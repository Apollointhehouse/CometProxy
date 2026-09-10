package dev.apollointhehouse.model

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import io.ktor.utils.io.*
import java.util.*

object SyncedEntityData {
    suspend fun pack(items: List<EntityDataItem<*>>?, channel: ByteWriteChannel) {
        if (items != null) {
            for (item in items) {
                writeDataItem(channel, item)
            }
        }

        channel.writeByte(0xFF.toByte())
    }

    private suspend fun <T> writeDataItem(channel: ByteWriteChannel, item: EntityDataItem<T>) {
        channel.writeByte((item.id and 0xFF).toByte())
        channel.writeByte((item.type and 0xFF).toByte())
        var metadata: Byte = 0
        val value = item.value

        if (value == null) {
            metadata = 1.toByte()
            channel.writeByte(metadata)
            return
        }

        packItem(item, channel, metadata, value)
    }

    private suspend fun <T> packItem(
        di: EntityDataItem<T>,
        channel: ByteWriteChannel,
        metadata: Byte,
        value: T & Any
    ): Unit = when (di.type) {
        0 -> {
            channel.writeByte(metadata)
            channel.writeByte(value as Byte)
        }

        1 -> {
            channel.writeByte(metadata)
            channel.writeShort(value as Short)
        }

        2 -> {
            channel.writeByte(metadata)
            channel.writeInt(value as Int)
        }

        3 -> {
            channel.writeByte(metadata)
            channel.writeFloat(value as Float)
        }

        4 -> {
            val s = value as String
            channel.writeByte(metadata)
            channel.writeJavaStringUTF8(s)
        }

        5 -> {
            val itemStack: ItemStack = value as ItemStack
            channel.writeByte(metadata)
            channel.writeShort(itemStack.itemID)
            channel.writeByte(itemStack.size)
            channel.writeShort(itemStack.meta)
            channel.writeCompressedCompoundTag(itemStack.tag!!)
        }

        6 -> {
            val chunkCoords: ChunkCoordinates = value as ChunkCoordinates
            channel.writeByte(metadata)
            channel.writeInt(chunkCoords.x)
            channel.writeInt(chunkCoords.y)
            channel.writeInt(chunkCoords.z)
        }

        7 -> {
            val uuid = value as UUID
            channel.writeByte(metadata)
            channel.writeLong(uuid.mostSignificantBits)
            channel.writeLong(uuid.leastSignificantBits)
        }

        else -> {}
    }

    suspend fun unpack(channel: ByteReadChannel): MutableList<EntityDataItem<*>> {
        val out: MutableList<EntityDataItem<*>> = mutableListOf()

        while (true) {
            val id = channel.readByte().toInt() and 0xFF

            if (id == 0xFF) break

            val type = channel.readByte().toInt() and 0xFF
            val metadata = channel.readByte()
            var item: EntityDataItem<*> =
                EntityDataItem(type, id, null)

            if ((metadata.toInt() and 1) == 0) {
                item = unpackItem(type, id, channel)
            }

            out.add(item)
        }

        return out
    }

    private suspend fun unpackItem(
        type: Int,
        id: Int,
        channel: ByteReadChannel
    ): EntityDataItem<*> = when (type) {
        0 -> EntityDataItem(type, id, channel.readByte())
        1 -> EntityDataItem(type, id, channel.readShort())
        2 -> EntityDataItem(type, id, channel.readInt())
        3 -> EntityDataItem(type, id, channel.readFloat())
        4 -> EntityDataItem(type, id, channel.readJavaStringUTF8(1024))
        5 -> {
            val itemId = channel.readShort()
            val itemCount = channel.readByte()
            val itemData = channel.readShort()
            val tag: CompoundTag? = channel.readCompressedCompoundTag()
            EntityDataItem(
                type,
                id,
                ItemStack(itemId, itemCount, itemData, tag)
            )
        }

        6 -> {
            val x = channel.readInt()
            val y = channel.readInt()
            val z = channel.readInt()
            EntityDataItem(
                type,
                id,
                ChunkCoordinates(x, y, z)
            )
        }

        7 -> {
            val msb = channel.readLong()
            val lsb = channel.readLong()
            EntityDataItem(type, id, UUID(msb, lsb))
        }
        else -> EntityDataItem(type, id, null as Any?)
    }

}