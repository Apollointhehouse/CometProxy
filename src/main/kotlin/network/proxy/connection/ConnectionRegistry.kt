package dev.apollointhehouse.network.proxy.connection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object ConnectionRegistry {
    val connections: StateFlow<Set<ConnectionContext>>
        field = MutableStateFlow<Set<ConnectionContext>>(emptySet())

    fun register(ctx: ConnectionContext) {
        connections.update { it + ctx }
    }

    fun unregister(ctx: ConnectionContext) {
        connections.update { list -> list.filterNotTo(mutableSetOf()) { it.id == ctx.id } }
    }
}