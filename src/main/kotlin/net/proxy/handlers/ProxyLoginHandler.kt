package dev.apollointhehouse.net.proxy.handlers

import dev.apollointhehouse.net.packet.PacketLogin
import dev.apollointhehouse.net.proxy.ConnectionRegistry
import dev.apollointhehouse.net.proxy.pipeline.PacketContext
import dev.apollointhehouse.net.proxy.pipeline.PacketHandler
import dev.apollointhehouse.net.proxy.session.ChatSession
import dev.apollointhehouse.net.proxy.session.PlayerSession
import dev.apollointhehouse.utils.crypt.RSA
import java.security.KeyPair
import kotlin.uuid.toKotlinUuid

class ProxyLoginHandler(private val proxyKeyPair: KeyPair) : PacketHandler<PacketLogin> {
    override suspend fun handle(context: PacketContext, packet: PacketLogin): PacketLogin {
        when (context.direction) {
            PacketContext.Direction.CLIENT_TO_SERVER -> {
                context.connection.session = PlayerSession(
                    chat = ChatSession(
                        realClientPublicKey = RSA.getPublicKey(packet.publicKey)
                    ),
                    username = packet.username,
                    uuid = packet.uuid.toKotlinUuid(),
                )

                packet.publicKey = RSA.getPublicKey(proxyKeyPair.public)
            }
            PacketContext.Direction.SERVER_TO_CLIENT -> {
                context.connection.session?.entityId = packet.playerEntityIdAndProtocolVersion

                ConnectionRegistry.register(context.connection)
            }
        }

        return packet
    }
}