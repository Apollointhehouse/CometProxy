package dev.apollointhehouse.data.nbt.tags

import java.io.DataInput

interface TagFactory<T : Tag<*>> {
    fun create(name: String?, dis: DataInput): T
}