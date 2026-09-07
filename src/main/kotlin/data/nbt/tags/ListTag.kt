package dev.apollointhehouse.data.nbt.tags

import dev.apollointhehouse.data.nbt.UnknownTagException
import java.io.DataInput
import java.io.DataOutput

class ListTag(name: String?, array: MutableList<Tag<*>> = mutableListOf<Tag<*>>()) :
    Tag<MutableList<Tag<*>>>(name, array) {
    private var tagType: TagType = TagType.End

    override fun write(dos: DataOutput) {
        tagType = if (value.isNotEmpty()) {
            value[0].type
        } else {
            TagType.Byte
        }

        dos.writeByte(tagType.value.toInt())
        dos.writeInt(value.size)

        for (value in value) {
            value.write(dos)
        }
    }

    override val type = TagType.List

    companion object : TagFactory<ListTag> {
        override fun create(name: String?, dis: DataInput): ListTag {
            val tagType = dis.readByte()
            val length = dis.readInt()
            val value = mutableListOf<Tag<*>>()

            repeat(length) {
                try {
                    val tagFactory: TagFactory<*> = TagType(tagType).factory

                    val tag = tagFactory.create(null, dis)
                    value.add(tag)
                } catch (_: IllegalArgumentException) {
                    throw UnknownTagException("Unknown tag type '$tagType'!")
                }
            }

            return ListTag(name, value)
        }
    }
}
