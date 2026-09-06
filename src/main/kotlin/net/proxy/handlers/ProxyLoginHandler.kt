package dev.apollointhehouse.net.proxy.handlers

import dev.apollointhehouse.net.packet.PacketLogin
import dev.apollointhehouse.net.proxy.ConnectionRegistry
import dev.apollointhehouse.net.proxy.pipeline.PacketContext
import dev.apollointhehouse.net.proxy.pipeline.PacketHandler
import dev.apollointhehouse.net.proxy.session.PlayerSession
import dev.apollointhehouse.utils.crypt.RSA
import java.security.KeyPair

class ProxyLoginHandler(private val proxyKeyPair: KeyPair) : PacketHandler<PacketLogin> {
    override suspend fun handle(context: PacketContext, packet: PacketLogin): PacketLogin {
        val session = context.connection.session ?: PlayerSession()
        context.connection.session = session

        when (context.direction) {
            PacketContext.Direction.CLIENT_TO_SERVER -> {
                session.chat.realClientPublicKey = RSA.getPublicKey(packet.publicKey)
                packet.publicKey = RSA.getPublicKey(proxyKeyPair.public)
                session.username = packet.username
            }
            PacketContext.Direction.SERVER_TO_CLIENT -> {
                session.entityId = packet.playerEntityIdAndProtocolVersion

                ConnectionRegistry.register(context.connection)
            }
        }

        return packet
    }
}