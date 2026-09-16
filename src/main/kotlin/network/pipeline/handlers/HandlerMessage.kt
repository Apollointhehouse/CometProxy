package dev.apollointhehouse.network.pipeline.handlers

import dev.apollointhehouse.network.packet.chat.PacketMessage
import dev.apollointhehouse.network.pipeline.PacketContext
import dev.apollointhehouse.network.pipeline.PacketHandler
import dev.apollointhehouse.network.crypto.AES
import dev.apollointhehouse.network.proxy.session.AuthSession
import org.apache.logging.log4j.kotlin.logger

class HandlerMessage : PacketHandler<PacketMessage> {
    private val log = logger()

    override suspend fun handle(context: PacketContext, packet: PacketMessage): PacketMessage {
        var msg: String

        if (!packet.encrypted) {
            msg = packet.message
        } else {
            val session = context.connection.session
            if (session !is AuthSession) return packet

            val aesKey = session.secretKey ?: return packet
            msg = AES.decrypt(packet.message, aesKey)
        }

        log.info { "${context.direction} Sent: $msg" }

        return packet
    }
}