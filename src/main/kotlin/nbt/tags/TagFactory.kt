package dev.apollointhehouse.nbt.tags

import java.io.DataInput

interface TagFactory<out T : Tag<*>> {
    fun create(name: String?, dis: DataInput): T
}