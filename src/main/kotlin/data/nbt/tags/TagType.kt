package dev.apollointhehouse.data.nbt.tags

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
        operator fun invoke(id: Byte): TagType = entries
            .getOrNull(id.toInt())
            ?: throw IllegalArgumentException("No NBT Tag type for id '$id'!")
    }
}