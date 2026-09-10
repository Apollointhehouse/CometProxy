package dev.apollointhehouse.nbt.tags

import java.io.DataInput
import java.io.DataOutput

data class EndTag(
    override val name: String?
) : Tag<Nothing?> {
    override val value = null

    override val type: TagType = TagType.End

    override fun write(dos: DataOutput) {}

    override fun copy(): EndTag = EndTag(name)

    companion object : TagFactory<EndTag> {
        override fun create(name: String?, dis: DataInput): EndTag = EndTag(name)
    }
}
