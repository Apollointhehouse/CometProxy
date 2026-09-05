package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput
import java.io.DataOutput

class FloatTag(name: String?, value: Float = 0.0f) : Tag<Float>(name, value) {
    override fun write(dos: DataOutput) {
        dos.writeFloat(value)
    }

    override val id get() = TagID.TAG_FLOAT.id

    companion object : TagFactory<FloatTag> {
        override fun create(name: String?, dis: DataInput): FloatTag {
            val value = dis.readFloat()

            return FloatTag(name, value)
        }
    }
}
