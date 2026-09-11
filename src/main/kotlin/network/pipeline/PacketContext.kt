package dev.apollointhehouse.network.pipeline

import dev.apollointhehouse.network.proxy.connection.ConnectionContext

class PacketContext(
    val direction: Direction,
    val connection: ConnectionContext,
) {
    enum class Direction { CLIENT_TO_SERVER, SERVER_TO_CLIENT }
}