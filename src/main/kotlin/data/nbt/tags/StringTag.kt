package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class StringTag(name: String?, value: String = "") : Tag<String>(name, value) {
    override fun write(dos: DataOutput) {
        dos.writeUTF(value)
    }

    override val id get() = TagID.TAG_STRING.id

    companion object : TagFactory<StringTag> {
        override fun create(name: String?, dis: DataInput): StringTag {
            val value = dis.readUTF()

            return StringTag(name, value)
        }
    }
}
