package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

data class IntTag(
    override val name: String?,
    override val value: Int
) : Tag<Int> {

    override val type: TagType = TagType.Int

    override fun write(dos: DataOutput) {
        dos.writeInt(value)
    }

    override fun copy(): IntTag = IntTag(name, value)

    companion object : TagFactory<IntTag> {
        override fun create(name: String?, dis: DataInput): IntTag =
            IntTag(name, dis.readInt())
    }
}
