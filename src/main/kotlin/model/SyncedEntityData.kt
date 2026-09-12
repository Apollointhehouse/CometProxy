package dev.apollointhehouse.model

import dev.apollointhehouse.nbt.tags.CompoundTag
import dev.apollointhehouse.network.extensions.readCompressedCompoundTag
import dev.apollointhehouse.network.extensions.readJavaStringUTF8
import dev.apollointhehouse.network.extensions.writeCompressedCompoundTag
import dev.apollointhehouse.network.extensions.writeJavaStringUTF8
import io.ktor.utils.io.*
import kotlinx.io.Sink
import kotlinx.io.writeFloat
import java.util.*

object SyncedEntityData {
    fun pack(items: List<EntityDataItem<*>>?, sink: Sink) {
        if (items != null) {
            for (item in items) {
                writeDataItem(sink, item)
            }
        }

        sink.writeByte(0xFF.toByte())
    }

    private fun <T> writeDataItem(sink: Sink, item: EntityDataItem<T>) {
        sink.writeByte((item.id and 0xFF).toByte())
        sink.writeByte((item.type and 0xFF).toByte())
        var metadata: Byte = 0
        val value = item.value

        if (value == null) {
            metadata = 1.toByte()
            sink.writeByte(metadata)
            return
        }

        packItem(item, sink, metadata, value)
    }

    private fun <T> packItem(
        di: EntityDataItem<T>,
        sink: Sink,
        metadata: Byte,
        value: T & Any
    ): Unit = when (di.type) {
        0 -> {
            sink.writeByte(metadata)
            sink.writeByte(value as Byte)
        }

        1 -> {
            sink.writeByte(metadata)
            sink.writeShort(value as Short)
        }

        2 -> {
            sink.writeByte(metadata)
            sink.writeInt(value as Int)
        }

        3 -> {
            sink.writeByte(metadata)
            sink.writeFloat(value as Float)
        }

        4 -> {
            val s = value as String
            sink.writeByte(metadata)
            sink.writeJavaStringUTF8(s)
        }

        5 -> {
            val itemStack: ItemStack = value as ItemStack
            sink.writeByte(metadata)
            sink.writeShort(itemStack.itemID)
            sink.writeByte(itemStack.size)
            sink.writeShort(itemStack.meta)
            sink.writeCompressedCompoundTag(itemStack.tag!!)
        }

        6 -> {
            val chunkCoords: ChunkCoordinates = value as ChunkCoordinates
            sink.writeByte(metadata)
            sink.writeInt(chunkCoords.x)
            sink.writeInt(chunkCoords.y)
            sink.writeInt(chunkCoords.z)
        }

        7 -> {
            val uuid = value as UUID
            sink.writeByte(metadata)
            sink.writeLong(uuid.mostSignificantBits)
            sink.writeLong(uuid.leastSignificantBits)
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