package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput

fun interface TagFactory<out T : Tag<*>> {
    fun create(name: String?, dis: DataInput): T
}