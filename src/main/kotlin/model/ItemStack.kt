package dev.apollointhehouse.model

import dev.apollointhehouse.nbt.tags.CompoundTag

data class ItemStack(
    val itemID: Short,
    val size: Byte,
    val meta: Short,
    val tag: CompoundTag?
)