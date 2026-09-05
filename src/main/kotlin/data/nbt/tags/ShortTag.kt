package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class ShortTag(name: String?, value: Short = 0) : Tag<Short>(name, value) {
    override fun write(dos: DataOutput) {
        dos.writeShort(value.toInt())
    }

    override val id get() =  TagID.TAG_SHORT.id

    companion object : TagFactory<ShortTag> {
        override fun create(name: String?, dis: DataInput): ShortTag {
            val value = dis.readShort()

            return ShortTag(name, value)
        }
    }
}
