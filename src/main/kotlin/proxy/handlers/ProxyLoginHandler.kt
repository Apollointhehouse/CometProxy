package dev.apollointhehouse.proxy.handlers

import dev.apollointhehouse.utils.crypt.RSA
import dev.apollointhehouse.packet.PacketLogin
import dev.apollointhehouse.proxy.pipeline.PacketContext
import dev.apollointhehouse.proxy.pipeline.PacketHandler
import java.security.KeyPair

class ProxyLoginHandler(private val proxyKeyPair: KeyPair) : PacketHandler<PacketLogin> {
    override suspend fun handle(context: PacketContext, packet: PacketLogin): PacketLogin {
        when (context.direction) {
            PacketContext.Direction.CLIENT_TO_SERVER -> {
                context.session.chat.realClientPublicKey = RSA.getPublicKey(packet.publicKey)
                packet.publicKey = RSA.getPublicKey(proxyKeyPair.public)
            }
            PacketContext.Direction.SERVER_TO_CLIENT -> {
                context.session.entityId = packet.playerEntityIdAndProtocolVersion
            }
        }
        return packet
    }
}