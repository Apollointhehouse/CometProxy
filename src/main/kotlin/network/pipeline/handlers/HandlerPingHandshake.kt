package dev.apollointhehouse.network.pipeline.handlers

import dev.apollointhehouse.network.packet.handshake.PacketDisconnect
import dev.apollointhehouse.network.packet.handshake.PacketPingHandshake
import dev.apollointhehouse.network.pipeline.PacketContext
import dev.apollointhehouse.network.pipeline.PacketHandler
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import org.apache.logging.log4j.kotlin.logger

class HandlerPingHandshake(private val config: ProxyConfig) : PacketHandler<PacketPingHandshake> {
    private val log = logger()

    override suspend fun handle(context: PacketContext, packet: PacketPingHandshake): PacketPingHandshake? {
        log.info { "Client Ping!" }

        val con = context.connection
        con.sendToClient(PacketDisconnect(reason = "§1\u000032769\u0000${config.btaVersion}\u0000${config.motd}\u00000\u0000100\u0000\u0000"))

        return null
    }
}