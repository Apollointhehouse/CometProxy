package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class CompoundTag(name: String?, value: MutableMap<String, Tag<*>> = mutableMapOf()) : Tag<MutableMap<String, Tag<*>>>(name, value) {
    override fun write(dos: DataOutput) {
        for (tag in value.values) {
            write(tag, dos)
        }

        dos.writeByte(0)
    }

    override val type = TagType.Compound

    companion object : TagFactory<CompoundTag> {
        override fun create(name: String?, dis: DataInput): CompoundTag {
            val value = mutableMapOf<String, Tag<*>>()

            while (true) {
                val tag = read(dis)

                if (tag.type == TagType.End) break

                value[tag.name ?: ""] = tag
            }

            return CompoundTag(name, value)
        }
    }
}
