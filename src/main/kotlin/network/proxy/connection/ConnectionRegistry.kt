package dev.apollointhehouse.network.proxy.connection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

object ConnectionRegistry {
    val connections: StateFlow<List<ConnectionContext>> field = MutableStateFlow<List<ConnectionContext>>(emptyList())

    fun register(ctx: ConnectionContext) {
        connections.update { it + ctx }
    }

    fun unregister(ctx: ConnectionContext) {
        connections.update { list -> list.filterNot { it.id == ctx.id } }
    }
}