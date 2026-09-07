package dev.apollointhehouse.data.nbt.tags

import dev.apollointhehouse.data.nbt.UnknownTagException
import java.io.DataInput
import java.io.DataOutput
import java.util.Objects

abstract class Tag<T>(
    val name: String? = null,
    val value: T
) {
    abstract fun write(dos: DataOutput)

    abstract val type: TagType

    override fun equals(other: Any?): Boolean =
        other is Tag<*> && value == other.value && type == other.type

    companion object {
        fun read(input: DataInput): Tag<*> {
            val id = input.readByte()

            try {
                val type = TagType(id)

                if (type == TagType.End) {
                    return EndTag(null)
                }

                val tagFactory = type.factory

                val name = input.readUTF()
                val tag = tagFactory.create(name, input)
                return tag
            } catch (_: IllegalArgumentException) {
                throw UnknownTagException("Unknown tag type '$id'!")
            }
        }

        fun write(tag: Tag<*>, output: DataOutput) {
            output.writeByte(tag.type.value.toInt())
            if (tag.type != TagType.End) {
                output.writeUTF(tag.name ?: "")
                tag.write(output)
            }
        }
    }

    override fun hashCode(): Int = Objects.hash(name ?: "", value, type.value)
}
