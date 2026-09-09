package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class LongArrayTag(
    override val name: String?,
    override val value: LongArray
) : Tag<LongArray>, Iterable<Long> {

    override val type: TagType = TagType.LongArray

    val size: Int get() = value.size

    operator fun get(index: Int): Long = value[index]

    operator fun set(index: Int, long: Long) {
        value[index] = long
    }

    override fun iterator(): Iterator<Long> = value.iterator()

    override fun write(dos: DataOutput) {
        dos.writeInt(value.size)
        val bytes = ByteArray(value.size * 8)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().put(value)
        dos.write(bytes)
    }

    override fun copy(): LongArrayTag = LongArrayTag(name, value.clone())

    companion object : TagFactory<LongArrayTag> {
        override fun create(name: String?, dis: DataInput): LongArrayTag {
            val length = dis.readInt()
            val value = LongArray(length)
            val bytes = ByteArray(length * 8)
            dis.readFully(bytes)
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().get(value)
            return LongArrayTag(name, value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as LongArrayTag

        if (name != other.name) return false
        if (!value.contentEquals(other.value)) return false
        if (type != other.type) return false
        if (size != other.size) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.contentHashCode()
        result = 31 * result + type.hashCode()
        result = 31 * result + size
        return result
    }
}
