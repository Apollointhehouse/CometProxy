package dev.apollointhehouse.nbt.tags

import java.io.DataInput
import java.io.DataOutput
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class ShortArrayTag(
    override val name: String?,
    override val value: ShortArray
) : Tag<ShortArray>, Iterable<Short> {

    override val type: TagType = TagType.ShortArray

    val size: Int get() = value.size

    operator fun get(index: Int): Short = value[index]

    operator fun set(index: Int, short: Short) {
        value[index] = short
    }

    override fun iterator(): Iterator<Short> = value.iterator()

    override fun write(dos: DataOutput) {
        dos.writeInt(value.size)
        val bytes = ByteArray(value.size * 2)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(value)
        dos.write(bytes)
    }

    override fun copy(): ShortArrayTag = ShortArrayTag(name, value.clone())

    companion object : TagFactory<ShortArrayTag> {
        override fun create(name: String?, dis: DataInput): ShortArrayTag {
            val length = dis.readInt()
            val value = ShortArray(length)
            val bytes = ByteArray(length * 2)
            dis.readFully(bytes)
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(value)
            return ShortArrayTag(name, value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ShortArrayTag

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
