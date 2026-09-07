package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class DoubleTag(name: String?, value: Double = 0.0) : Tag<Double>(name, value) {
    override fun write(dos: DataOutput) {
        dos.writeDouble(value)
    }

    override val type = TagType.Double

    companion object : TagFactory<DoubleTag> {
        override fun create(name: String?, dis: DataInput): DoubleTag {
            val value = dis.readDouble()

            return DoubleTag(name, value)
        }
    }
}
