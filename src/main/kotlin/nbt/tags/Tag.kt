package dev.apollointhehouse.nbt.tags

import dev.apollointhehouse.nbt.UnknownTagException
import java.io.DataInput
import java.io.DataOutput

interface Tag<T> {
    val name: String?
    val value: T

    fun write(dos: DataOutput)

    val type: TagType

    fun copy(): Tag<T>

    companion object {
        fun read(input: DataInput): Tag<*> {
            val id = input.readByte()
            if (id == 0.toByte()) {
                return EndTag(null)
            }

            val type = TagType.fromID(id) ?: throw UnknownTagException("Unknown tag type '$id'!")
            val name = input.readUTF()
            return type.factory.create(name, input)
        }

        fun write(tag: Tag<*>, output: DataOutput) {
            output.writeByte(tag.type.value.toInt())
            if (tag.type != TagType.End) {
                output.writeUTF(tag.name ?: "")
                tag.write(output)
            }
        }
    }
}
