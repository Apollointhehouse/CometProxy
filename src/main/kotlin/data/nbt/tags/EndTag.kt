package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class EndTag(name: String?) : Tag<Nothing?>(name, null) {
    override fun write(dos: DataOutput) {}

    override val type = TagType.End

    companion object : TagFactory<EndTag> {
        override fun create(name: String?, dis: DataInput): EndTag {
            return EndTag(name)
        }
    }
}
