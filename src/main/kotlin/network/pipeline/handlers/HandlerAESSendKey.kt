package dev.apollointhehouse.network.pipeline.handlers

import dev.apollointhehouse.network.packet.auth.PacketAESSendKey
import dev.apollointhehouse.network.pipeline.PacketContext
import dev.apollointhehouse.network.pipeline.PacketHandler
import dev.apollointhehouse.network.crypto.RSA
import dev.apollointhehouse.network.proxy.session.AuthSession
import java.security.KeyPair
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

class HandlerAESSendKey(private val proxyKeyPair: KeyPair) : PacketHandler<PacketAESSendKey> {
    override suspend fun handle(context: PacketContext, packet: PacketAESSendKey): PacketAESSendKey {
        val rawAesKeyBytes = RSA.decrypt(packet.key, proxyKeyPair.private)

        val byteKey = Base64.decode(rawAesKeyBytes)
        val secretKey = SecretKeySpec(byteKey, "AES")

        val session = context.connection.session ?: error("No player session")
        if (session !is AuthSession) error("Session is not authenticated")

        session.secretKey = secretKey

        packet.key = RSA.encrypt(rawAesKeyBytes, session.clientPublicKey)
        return packet
    }
}