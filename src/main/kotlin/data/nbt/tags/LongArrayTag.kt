package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput
import java.nio.ByteBuffer
import java.nio.ByteOrder

class LongArrayTag(name: String?, array: LongArray = LongArray(0)) : Tag<LongArray>(name, array) {
    override fun write(dos: DataOutput) {
        dos.writeInt(value.size)
        val bytes = ByteArray(value.size * 8)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asLongBuffer().put(value)
        dos.write(bytes)
    }

    override val id get() = TagID.TAG_LONG_ARRAY.id

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
}
