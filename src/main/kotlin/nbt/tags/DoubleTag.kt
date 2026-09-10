package dev.apollointhehouse.nbt.tags

import java.io.DataInput
import java.io.DataOutput

data class DoubleTag(
    override val name: String?,
    override val value: Double
) : Tag<Double> {

    override val type: TagType = TagType.Double

    override fun write(dos: DataOutput) {
        dos.writeDouble(value)
    }

    override fun copy(): DoubleTag = DoubleTag(name, value)

    companion object : TagFactory<DoubleTag> {
        override fun create(name: String?, dis: DataInput): DoubleTag =
            DoubleTag(name, dis.readDouble())
    }
}
