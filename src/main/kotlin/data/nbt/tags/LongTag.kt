package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class LongTag(name: String?, value: Long = 0L) : Tag<Long>(name, value) {
    override fun write(dos: DataOutput) {
        dos.writeLong(value)
    }

    override val id get() = TagID.TAG_LONG.id

    companion object : TagFactory<LongTag> {
        override fun create(name: String?, dis: DataInput): LongTag {
            val value = dis.readLong()

            return LongTag(name, value)
        }
    }
}
