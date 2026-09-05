package dev.apollointhehouse.proxy.pipeline

import dev.apollointhehouse.proxy.session.PlayerSession
import dev.apollointhehouse.packet.Packet

class PacketContext(
    val direction: Direction,
    val netContext: NetContext,
) {
    val session: PlayerSession = netContext.session

    enum class Direction { CLIENT_TO_SERVER, SERVER_TO_CLIENT }

    fun sendToClient(packet: Packet) = netContext.sendToClient(packet)
    fun sendToServer(packet: Packet) = netContext.sendToServer(packet)
}