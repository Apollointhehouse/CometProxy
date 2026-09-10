package dev.apollointhehouse.network.proxy.pipeline

class PacketContext(
    val direction: Direction,
    val connection: ConnectionContext,
) {
    enum class Direction { CLIENT_TO_SERVER, SERVER_TO_CLIENT }
}