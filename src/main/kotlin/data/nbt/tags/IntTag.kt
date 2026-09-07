package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class IntTag(name: String?, value: Int = 0) : Tag<Int>(name, value) {
    override fun write(dos: DataOutput) {
        dos.writeInt(value)
    }

    override val type = TagType.Int

    companion object : TagFactory<IntTag> {
        override fun create(name: String?, dis: DataInput): IntTag {
            val value = dis.readInt()

            return IntTag(name, value)
        }
    }
}
