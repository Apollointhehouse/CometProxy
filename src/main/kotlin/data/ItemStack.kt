package dev.apollointhehouse.data

import dev.apollointhehouse.data.nbt.tags.CompoundTag

data class ItemStack(
    val itemID: Short,
    val size: Byte,
    val meta: Short,
    val tag: CompoundTag?
)