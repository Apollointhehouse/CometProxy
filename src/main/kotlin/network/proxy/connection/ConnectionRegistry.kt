package dev.apollointhehouse.network.proxy.connection

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object ConnectionRegistry {
    private val _connections = MutableStateFlow<List<ConnectionContext>>(emptyList())
    val connections = _connections.asStateFlow()

    fun register(ctx: ConnectionContext) {
        _connections.update { it + ctx }
    }

    fun unregister(ctx: ConnectionContext) {
        _connections.update { list -> list.filterNot { it.id == ctx.id } }
    }
}