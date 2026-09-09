package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

data class ByteArrayTag(
    override val name: String?,
    override val value: ByteArray
) : Tag<ByteArray>, Iterable<Byte> {

    override val type: TagType = TagType.ByteArray

    val size: Int get() = value.size

    operator fun get(index: Int): Byte = value[index]

    operator fun set(index: Int, byte: Byte) {
        value[index] = byte
    }

    override fun iterator(): Iterator<Byte> = value.iterator()

    override fun write(dos: DataOutput) {
        dos.writeInt(value.size)
        dos.write(value)
    }

    override fun copy(): ByteArrayTag = ByteArrayTag(name, value.clone())

    companion object : TagFactory<ByteArrayTag> {
        override fun create(name: String?, dis: DataInput): ByteArrayTag {
            val length = dis.readInt()
            val value = ByteArray(length)
            dis.readFully(value)
            return ByteArrayTag(name, value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayTag

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
