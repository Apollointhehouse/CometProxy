package dev.apollointhehouse.network.pipeline.handlers

import dev.apollointhehouse.network.packet.auth.PacketLogin
import dev.apollointhehouse.network.proxy.connection.ConnectionRegistry
import dev.apollointhehouse.network.pipeline.PacketContext
import dev.apollointhehouse.network.pipeline.PacketHandler
import dev.apollointhehouse.network.crypto.RSA
import dev.apollointhehouse.network.proxy.session.PreAuthSession
import dev.apollointhehouse.network.proxy.session.upgrade
import java.security.KeyPair
import kotlin.uuid.toKotlinUuid

class HandlerLogin(private val proxyKeyPair: KeyPair) : PacketHandler<PacketLogin> {
    override suspend fun handle(context: PacketContext, packet: PacketLogin): PacketLogin {
        when (context.direction) {
            PacketContext.Direction.CLIENT_TO_SERVER -> {
                context.connection.session = PreAuthSession(
                    username = packet.username,
                    uuid = packet.uuid.toKotlinUuid(),
                    clientPublicKey = RSA.getPublicKey(packet.publicKey)
                )

                packet.publicKey = RSA.getPublicKey(proxyKeyPair.public)
            }
            PacketContext.Direction.SERVER_TO_CLIENT -> {
                val session = context.connection.session
                if (session !is PreAuthSession) return packet

                context.connection.session = session.upgrade(
                    entityID = packet.playerEntityIdAndProtocolVersion,
                )

                ConnectionRegistry.register(context.connection)
            }
        }

        return packet
    }
}