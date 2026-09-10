package dev.apollointhehouse.network.proxy.handlers

import dev.apollointhehouse.network.packet.auth.PacketLogin
import dev.apollointhehouse.network.proxy.ConnectionRegistry
import dev.apollointhehouse.network.proxy.pipeline.PacketContext
import dev.apollointhehouse.network.proxy.pipeline.PacketHandler
import dev.apollointhehouse.network.proxy.session.ChatSession
import dev.apollointhehouse.network.proxy.session.PlayerSession
import dev.apollointhehouse.network.crypto.RSA
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