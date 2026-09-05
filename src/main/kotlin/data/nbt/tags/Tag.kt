package dev.apollointhehouse.data.nbt.tags

import dev.apollointhehouse.data.nbt.UnknownTagException
import java.io.DataInput
import java.io.DataOutput

abstract class Tag<T>(private var name: String? = null, var value: T) {
    abstract fun write(dos: DataOutput)

    abstract val id: Byte

    val tagName: String
        get() = (if (this.name == null) "" else this.name)!!

    override fun equals(other: Any?): Boolean {
        return other is Tag<*> && (this.value == other.value)
    }

    enum class TagID(val id: Byte, val factory: TagFactory<*>) {
        TAG_END(0, EndTag),
        TAG_BYTE(1, ByteTag),
        TAG_SHORT(2, ShortTag),
        TAG_INT(3, IntTag),
        TAG_LONG(4, LongTag),
        TAG_FLOAT(5, FloatTag),
        TAG_DOUBLE(6, DoubleTag),
        TAG_BYTE_ARRAY(7, ByteArrayTag),
        TAG_STRING(8, StringTag),
        TAG_LIST(9, ListTag),
        TAG_COMPOUND(10, CompoundTag),
        TAG_SHORT_ARRAY(11, ShortArrayTag),
        TAG_DOUBLE_ARRAY(12, DoubleArrayTag),
        TAG_LONG_ARRAY(13, LongArrayTag);

        companion object {
            operator fun invoke(id: Byte): TagID = entries
                .find { id == it.id }
                ?: throw IllegalArgumentException("No NBT Tag type for id '$id'!")
        }
    }

    companion object {
        fun readNamedTag(dis: DataInput): Tag<*> {
            val typeId = dis.readByte()
            if (typeId == TagID.TAG_END.id) {
                return EndTag(null)
            }

            try {
                val id = TagID(typeId)
                val tagFactory = id.factory

                val name = dis.readUTF()
                val tag = tagFactory.create(name, dis)
                return tag
            } catch (_: IllegalArgumentException) {
                throw UnknownTagException("Unknown tag type '$typeId'!")
            }
        }

        fun writeNamedTag(tag: Tag<*>, dos: DataOutput) {
            dos.writeByte(tag.id.toInt())
            if (tag.id != TagID.TAG_END.id) {
                dos.writeUTF(tag.tagName)
                tag.write(dos)
            }
        }

    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        result = 31 * result + id
        result = 31 * result + tagName.hashCode()
        return result
    }
}
