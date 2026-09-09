package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

data class StringTag(
    override val name: String?,
    override val value: String
) : Tag<String> {

    override val type: TagType = TagType.String

    override fun write(dos: DataOutput) {
        dos.writeUTF(value)
    }

    override fun copy(): StringTag = StringTag(name, value)

    companion object : TagFactory<StringTag> {
        override fun create(name: String?, dis: DataInput): StringTag =
            StringTag(name, dis.readUTF())
    }
}
