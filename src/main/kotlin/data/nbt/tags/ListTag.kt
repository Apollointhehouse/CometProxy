package dev.apollointhehouse.data.nbt.tags

import dev.apollointhehouse.data.nbt.UnknownTagException
import java.io.DataInput
import java.io.DataOutput
import java.lang.IllegalArgumentException

class ListTag(name: String?, array: MutableList<Tag<*>> = mutableListOf<Tag<*>>()) :
    Tag<MutableList<Tag<*>>>(name, array) {
    private var tagType: Byte = 0

    override fun write(dos: DataOutput) {
        if (value.isNotEmpty()) {
            this.tagType = value[0].id
        } else {
            this.tagType = 1
        }

        dos.writeByte(this.tagType.toInt())
        dos.writeInt(value.size)

        for (i in value.indices) {
            value[i].write(dos)
        }
    }

    override val id get() = TagID.TAG_LIST.id

    companion object : TagFactory<ListTag> {
        override fun create(name: String?, dis: DataInput): ListTag {
            val tagType = dis.readByte()
            val length = dis.readInt()
            val value = mutableListOf<Tag<*>>()

            repeat(length) {
                try {
                    val tagFactory: TagFactory<*> = TagID(tagType).factory

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
