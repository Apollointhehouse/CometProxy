package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput
import java.nio.ByteBuffer
import java.nio.ByteOrder

data class DoubleArrayTag(
    override val name: String?,
    override val value: DoubleArray
) : Tag<DoubleArray>, Iterable<Double> {

    override val type: TagType = TagType.DoubleArray

    val size: Int get() = value.size

    operator fun get(index: Int): Double = value[index]

    operator fun set(index: Int, double: Double) {
        value[index] = double
    }

    override fun iterator(): Iterator<Double> = value.iterator()

    override fun write(dos: DataOutput) {
        dos.writeInt(value.size)
        val bytes = ByteArray(value.size * 8)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().put(value)
        dos.write(bytes)
    }

    override fun copy(): DoubleArrayTag = DoubleArrayTag(name, value.clone())

    companion object : TagFactory<DoubleArrayTag> {
        override fun create(name: String?, dis: DataInput): DoubleArrayTag {
            val length = dis.readInt()
            val value = DoubleArray(length)
            val bytes = ByteArray(length * 8)
            dis.readFully(bytes)
            ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().get(value)
            return DoubleArrayTag(name, value)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DoubleArrayTag

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
