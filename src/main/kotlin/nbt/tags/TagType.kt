package dev.apollointhehouse.nbt.tags

enum class TagType(val value: Byte, val factory: TagFactory<*>) {
    End(0, EndTag),
    Byte(1, ByteTag),
    Short(2, ShortTag),
    Int(3, IntTag),
    Long(4, LongTag),
    Float(5, FloatTag),
    Double(6, DoubleTag),
    ByteArray(7, ByteArrayTag),
    String(8, StringTag),
    List(9, ListTag),
    Compound(10, CompoundTag),
    ShortArray(11, ShortArrayTag),
    DoubleArray(12, DoubleArrayTag),
    LongArray(13, LongArrayTag);

    companion object {
        private val BY_ID = Array(14) { id -> entries.firstOrNull { it.value.toInt() == id } }

        operator fun invoke(id: Byte): TagType =
            fromID(id) ?: throw IllegalArgumentException("No NBT Tag type for id '$id'!")

        fun fromID(id: Byte): TagType? =
            BY_ID.getOrNull(id.toInt())
    }
}
