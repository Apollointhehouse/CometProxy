package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class ByteArrayTag(name: String?, array: ByteArray = ByteArray(0)) : Tag<ByteArray>(name, array) {
    override fun write(dos: DataOutput) {
        dos.writeInt(value.size)
        dos.write(value)
    }

    override val type = TagType.ByteArray

    companion object : TagFactory<ByteArrayTag> {
        override fun create(name: String?, dis: DataInput): ByteArrayTag {
            val length = dis.readInt()
            val value = ByteArray(length)
            dis.readFully(value)

            return ByteArrayTag(name, value)
        }
    }
}
