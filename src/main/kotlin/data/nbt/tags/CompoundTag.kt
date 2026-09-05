package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class CompoundTag(name: String?, value: MutableMap<String, Tag<*>> = mutableMapOf()) : Tag<MutableMap<String, Tag<*>>>(name, value) {
    override fun write(dos: DataOutput) {
        for (tag in value.values) {
            writeNamedTag(tag, dos)
        }

        dos.writeByte(0)
    }

    override val id get() = TagID.TAG_COMPOUND.id

    companion object : TagFactory<CompoundTag> {
        override fun create(name: String?, dis: DataInput): CompoundTag {
            val value = mutableMapOf<String, Tag<*>>()

            while (true) {
                val tag = readNamedTag(dis)

                if (tag.id.toInt() == 0) break

                value[tag.tagName] = tag
            }

            return CompoundTag(name, value)
        }
    }
}
