package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput
import java.nio.ByteBuffer
import java.nio.ByteOrder


class ShortArrayTag(name: String?, array: ShortArray = ShortArray(0)) : Tag<ShortArray>(name, array) {
    override fun write(dos: DataOutput) {
        dos.writeInt(value.size)
        val bytes = ByteArray(value.size * 2)
        ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().put(value)
        dos.write(bytes)
    }

    override val type = TagType.ShortArray

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
}
