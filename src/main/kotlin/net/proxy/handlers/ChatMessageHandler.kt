package dev.apollointhehouse.net.proxy.handlers

import dev.apollointhehouse.net.packet.PacketMessage
import dev.apollointhehouse.net.proxy.pipeline.PacketContext
import dev.apollointhehouse.net.proxy.pipeline.PacketHandler
import dev.apollointhehouse.utils.crypt.AES
import org.apache.logging.log4j.kotlin.logger

class ChatMessageHandler : PacketHandler<PacketMessage> {
    override suspend fun handle(context: PacketContext, packet: PacketMessage): PacketMessage {
        var msg: String

        if (!packet.encrypted) {
            msg = packet.message
        } else {
            val aesKey = context.connection.session?.chat?.sharedAesKey ?: return packet
            msg = AES.decrypt(packet.message, aesKey)
        }

        logger.info { "${context.direction} Sent: $msg" }

        return packet
    }
}