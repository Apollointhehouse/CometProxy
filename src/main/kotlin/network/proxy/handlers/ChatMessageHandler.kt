package dev.apollointhehouse.network.proxy.handlers

import dev.apollointhehouse.network.packet.chat.PacketMessage
import dev.apollointhehouse.network.proxy.pipeline.PacketContext
import dev.apollointhehouse.network.proxy.pipeline.PacketHandler
import dev.apollointhehouse.network.crypto.AES
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