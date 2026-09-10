package dev.apollointhehouse.nbt.tags

import dev.apollointhehouse.nbt.UnknownTagException
import java.io.DataInput
import java.io.DataOutput

data class ListTag(
    override val name: String?,
    override val value: MutableList<Tag<*>> = mutableListOf(),
    var tagType: TagType = TagType.End
) : Tag<MutableList<Tag<*>>>, Iterable<Tag<*>> {

    init {
        if (value.isNotEmpty() && tagType == TagType.End) {
            tagType = value[0].type
        }
    }

    override val type: TagType = TagType.List

    val size: Int get() = value.size

    fun isEmpty(): Boolean = value.isEmpty()

    fun isNotEmpty(): Boolean = value.isNotEmpty()

    operator fun get(index: Int): Tag<*> = value[index]

    operator fun set(index: Int, tag: Tag<*>) {
        if (value.isEmpty()) {
            tagType = tag.type
        }
        value[index] = tag
    }

    fun add(tag: Tag<*>): Boolean {
        if (value.isEmpty() && tagType == TagType.End) {
            tagType = tag.type
        }
        return value.add(tag)
    }

    fun removeAt(index: Int): Tag<*> = value.removeAt(index)

    fun clear() {
        value.clear()
        tagType = TagType.End
    }

    fun getCompound(index: Int): CompoundTag? = value.getOrNull(index) as? CompoundTag

    fun getList(index: Int): ListTag? = value.getOrNull(index) as? ListTag

    fun getString(index: Int): String? = (value.getOrNull(index) as? StringTag)?.value

    fun getInt(index: Int): Int? = (value.getOrNull(index) as? IntTag)?.value

    fun getByte(index: Int): Byte? = (value.getOrNull(index) as? ByteTag)?.value

    fun getShort(index: Int): Short? = (value.getOrNull(index) as? ShortTag)?.value

    fun getLong(index: Int): Long? = (value.getOrNull(index) as? LongTag)?.value

    fun getFloat(index: Int): Float? = (value.getOrNull(index) as? FloatTag)?.value

    fun getDouble(index: Int): Double? = (value.getOrNull(index) as? DoubleTag)?.value

    fun getByteArray(index: Int): ByteArray? = (value.getOrNull(index) as? ByteArrayTag)?.value

    override fun iterator(): Iterator<Tag<*>> = value.iterator()

    override fun write(dos: DataOutput) {
        val effectiveType = when {
            value.isNotEmpty() -> value[0].type
            tagType != TagType.End -> tagType
            else -> TagType.End
        }

        dos.writeByte(effectiveType.value.toInt())
        dos.writeInt(value.size)

        for (item in value) {
            item.write(dos)
        }
    }

    override fun copy(): ListTag {
        val copiedList = mutableListOf<Tag<*>>()
        for (item in value) {
            copiedList.add(item.copy())
        }
        return ListTag(name, copiedList, tagType)
    }

    companion object : TagFactory<ListTag> {
        override fun create(name: String?, dis: DataInput): ListTag {
            val tagTypeByte = dis.readByte()
            val length = dis.readInt()
            val resolvedType = TagType.fromID(tagTypeByte)
                ?: throw UnknownTagException("Unknown tag type '$tagTypeByte'!")

            val resultList = mutableListOf<Tag<*>>()
            if (length > 0 && resolvedType != TagType.End) {
                val factory = resolvedType.factory
                repeat(length) {
                    resultList.add(factory.create(null, dis))
                }
            }

            return ListTag(name, resultList, resolvedType)
        }
    }
}
