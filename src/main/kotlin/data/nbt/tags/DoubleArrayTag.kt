package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput
import java.nio.ByteBuffer
import java.nio.ByteOrder

class DoubleArrayTag(name: String?, array: DoubleArray = DoubleArray(0)) : Tag<DoubleArray>(name, array) {
    override fun write(dos: DataOutput) {
        dos.writeInt(value.size)
        val bytes = ByteArray(value.size * 8)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().put(value)
        dos.write(bytes)
    }

    override val type = TagType.DoubleArray

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
}
