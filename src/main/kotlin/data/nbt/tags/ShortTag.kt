package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

data class ShortTag(
    override val name: String?,
    override val value: Short
) : Tag<Short> {

    override val type: TagType = TagType.Short

    override fun write(dos: DataOutput) {
        dos.writeShort(value.toInt())
    }

    override fun copy(): ShortTag = ShortTag(name, value)

    companion object : TagFactory<ShortTag> {
        override fun create(name: String?, dis: DataInput): ShortTag =
            ShortTag(name, dis.readShort())
    }
}
