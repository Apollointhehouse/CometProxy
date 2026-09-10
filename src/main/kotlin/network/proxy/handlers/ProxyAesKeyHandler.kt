package dev.apollointhehouse.network.proxy.handlers

import dev.apollointhehouse.network.packet.auth.PacketAESSendKey
import dev.apollointhehouse.network.proxy.pipeline.PacketContext
import dev.apollointhehouse.network.proxy.pipeline.PacketHandler
import dev.apollointhehouse.network.crypto.RSA
import java.security.KeyPair
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

class ProxyAesKeyHandler(private val proxyKeyPair: KeyPair) : PacketHandler<PacketAESSendKey> {
    override suspend fun handle(context: PacketContext, packet: PacketAESSendKey): PacketAESSendKey {
        val rawAesKeyBytes = RSA.decrypt(packet.key, proxyKeyPair.private)

        val byteKey = Base64.decode(rawAesKeyBytes)
        val secretKey = SecretKeySpec(byteKey, "AES")

        val session = context.connection.session ?: error("No player session")

        session.chat.sharedAesKey = secretKey

        val realClientKey = session.chat.realClientPublicKey
            ?: error("Login packet wasn't intercepted before AES key arrived")

        packet.key = RSA.encrypt(rawAesKeyBytes, realClientKey)
        return packet
    }
}