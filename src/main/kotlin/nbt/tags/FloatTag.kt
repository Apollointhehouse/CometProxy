package dev.apollointhehouse.nbt.tags

import java.io.DataInput
import java.io.DataOutput

data class FloatTag(
    override val name: String?,
    override val value: Float
) : Tag<Float> {

    override val type: TagType = TagType.Float

    override fun write(dos: DataOutput) {
        dos.writeFloat(value)
    }

    override fun copy(): FloatTag = FloatTag(name, value)

    companion object : TagFactory<FloatTag> {
        override fun create(name: String?, dis: DataInput): FloatTag =
            FloatTag(name, dis.readFloat())
    }
}
