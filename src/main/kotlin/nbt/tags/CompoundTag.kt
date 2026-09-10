package dev.apollointhehouse.nbt.tags

import dev.apollointhehouse.nbt.tags.Tag.Companion.read
import java.io.DataInput
import java.io.DataOutput

data class CompoundTag(
    override val name: String?,
    override val value: MutableMap<String, Tag<*>> = mutableMapOf()
) : Tag<MutableMap<String, Tag<*>>>, Iterable<Tag<*>> {

    override val type: TagType = TagType.Compound

    val size: Int get() = value.size

    fun isEmpty(): Boolean = value.isEmpty()

    fun isNotEmpty(): Boolean = value.isNotEmpty()

    val keys: Set<String> get() = value.keys

    val tags: Collection<Tag<*>> get() = value.values

    val entries: Set<Map.Entry<String, Tag<*>>> get() = value.entries

    operator fun contains(key: String): Boolean = value.containsKey(key)

    fun contains(key: String, type: TagType): Boolean = value[key]?.type == type

    operator fun get(key: String): Tag<*>? = value[key]

    fun getTag(key: String): Tag<*>? = value[key]

    operator fun set(key: String, tag: Tag<*>) {
        put(key, tag)
    }

    operator fun set(key: String, str: String) = putString(key, str)
    operator fun set(key: String, int: Int) = putInt(key, int)
    operator fun set(key: String, long: Long) = putLong(key, long)
    operator fun set(key: String, byte: Byte) = putByte(key, byte)
    operator fun set(key: String, short: Short) = putShort(key, short)
    operator fun set(key: String, float: Float) = putFloat(key, float)
    operator fun set(key: String, double: Double) = putDouble(key, double)
    operator fun set(key: String, bool: Boolean) = putBoolean(key, bool)
    operator fun set(key: String, bytes: ByteArray) = putByteArray(key, bytes)

    fun put(tag: Tag<*>) {
        val tagName = tag.name ?: throw IllegalArgumentException("Cannot put nameless tag without explicit key")
        value[tagName] = tag
    }

    fun put(key: String, tag: Tag<*>) {
        value[key] = tag
    }

    fun putByte(key: String, byte: Byte) {
        value[key] = ByteTag(key, byte)
    }

    fun putShort(key: String, short: Short) {
        value[key] = ShortTag(key, short)
    }

    fun putInt(key: String, int: Int) {
        value[key] = IntTag(key, int)
    }

    fun putLong(key: String, long: Long) {
        value[key] = LongTag(key, long)
    }

    fun putFloat(key: String, float: Float) {
        value[key] = FloatTag(key, float)
    }

    fun putDouble(key: String, double: Double) {
        value[key] = DoubleTag(key, double)
    }

    fun putString(key: String, string: String) {
        value[key] = StringTag(key, string)
    }

    fun putByteArray(key: String, array: ByteArray) {
        value[key] = ByteArrayTag(key, array)
    }

    fun putShortArray(key: String, array: ShortArray) {
        value[key] = ShortArrayTag(key, array)
    }

    fun putLongArray(key: String, array: LongArray) {
        value[key] = LongArrayTag(key, array)
    }

    fun putDoubleArray(key: String, array: DoubleArray) {
        value[key] = DoubleArrayTag(key, array)
    }

    fun putCompound(key: String, compound: CompoundTag) {
        value[key] = compound
    }

    fun putList(key: String, list: ListTag) {
        value[key] = list
    }

    fun putBoolean(key: String, bool: Boolean) {
        putByte(key, if (bool) 1.toByte() else 0.toByte())
    }

    fun getByte(key: String, default: Byte = 0): Byte =
        (value[key] as? ByteTag)?.value ?: default

    fun getShort(key: String, default: Short = 0): Short =
        (value[key] as? ShortTag)?.value ?: default

    fun getInt(key: String, default: Int = 0): Int =
        (value[key] as? IntTag)?.value ?: default

    fun getLong(key: String, default: Long = 0L): Long =
        (value[key] as? LongTag)?.value ?: default

    fun getFloat(key: String, default: Float = 0f): Float =
        (value[key] as? FloatTag)?.value ?: default

    fun getDouble(key: String, default: Double = 0.0): Double =
        (value[key] as? DoubleTag)?.value ?: default

    fun getString(key: String, default: String = ""): String =
        (value[key] as? StringTag)?.value ?: default

    fun getByteArray(key: String): ByteArray =
        (value[key] as? ByteArrayTag)?.value ?: ByteArray(0)

    fun getShortArray(key: String): ShortArray =
        (value[key] as? ShortArrayTag)?.value ?: ShortArray(0)

    fun getLongArray(key: String): LongArray =
        (value[key] as? LongArrayTag)?.value ?: LongArray(0)

    fun getDoubleArray(key: String): DoubleArray =
        (value[key] as? DoubleArrayTag)?.value ?: DoubleArray(0)

    fun getCompound(key: String): CompoundTag? =
        value[key] as? CompoundTag

    fun getList(key: String): ListTag? =
        value[key] as? ListTag

    fun getBoolean(key: String, default: Boolean = false): Boolean =
        (value[key] as? ByteTag)?.let { it.value != 0.toByte() } ?: default

    fun remove(key: String): Tag<*>? = value.remove(key)

    fun clear() = value.clear()

    override fun iterator(): Iterator<Tag<*>> = value.values.iterator()

    override fun write(dos: DataOutput) {
        for ([key, tag] in value) {
            dos.writeByte(tag.type.value.toInt())
            if (tag.type != TagType.End) {
                dos.writeUTF(key)
                tag.write(dos)
            }
        }
        dos.writeByte(0)
    }

    override fun copy(): CompoundTag {
        val copiedMap = mutableMapOf<String, Tag<*>>()
        for ([key, value] in value) {
            copiedMap[key] = value.copy()
        }
        return CompoundTag(name, copiedMap)
    }

    companion object : TagFactory<CompoundTag> {
        override fun create(name: String?, dis: DataInput): CompoundTag {
            val map = mutableMapOf<String, Tag<*>>()

            while (true) {
                val tag = read(dis)
                if (tag.type == TagType.End) break
                map[tag.name ?: ""] = tag
            }

            return CompoundTag(name, map)
        }
    }
}
