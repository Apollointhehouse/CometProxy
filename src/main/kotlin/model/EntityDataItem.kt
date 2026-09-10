package dev.apollointhehouse.model

data class EntityDataItem<T>(val type: Int, val id: Int, var value: T?)