package dev.apollointhehouse.network.proxy.connection

import dev.apollointhehouse.network.proxy.session.PlayerSession
import kotlin.uuid.Uuid

data class ConnectionContext(
    val client: ProxyConnection,
    val server: ProxyConnection,
    var session: PlayerSession? = null,
    val id: Uuid = Uuid.random()
) : AutoCloseable {
    override fun close() {
        client.close()
        server.close()
    }
}