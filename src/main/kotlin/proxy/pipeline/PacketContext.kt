package dev.apollointhehouse.proxy.pipeline

import dev.apollointhehouse.proxy.session.PlayerSession
import dev.apollointhehouse.packet.Packet

class PacketContext(
    val direction: Direction,
    val connection: ConnectionContext,
) {
    val session: PlayerSession = connection.session

    enum class Direction { CLIENT_TO_SERVER, SERVER_TO_CLIENT }

    fun sendToClient(packet: Packet) = connection.sendToClient(packet)
    fun sendToServer(packet: Packet) = connection.sendToServer(packet)
}