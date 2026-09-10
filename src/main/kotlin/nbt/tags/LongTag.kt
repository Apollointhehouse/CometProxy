package dev.apollointhehouse.nbt.tags

import java.io.DataInput
import java.io.DataOutput

data class LongTag(
    override val name: String?,
    override val value: Long
) : Tag<Long> {

    override val type: TagType = TagType.Long

    override fun write(dos: DataOutput) {
        dos.writeLong(value)
    }

    override fun copy(): LongTag = LongTag(name, value)

    companion object : TagFactory<LongTag> {
        override fun create(name: String?, dis: DataInput): LongTag =
            LongTag(name, dis.readLong())
    }
}
