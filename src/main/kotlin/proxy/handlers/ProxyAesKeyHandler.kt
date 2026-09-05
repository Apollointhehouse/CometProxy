package dev.apollointhehouse.proxy.handlers

import dev.apollointhehouse.packet.PacketAESSendKey
import dev.apollointhehouse.proxy.pipeline.PacketContext
import dev.apollointhehouse.proxy.pipeline.PacketHandler
import dev.apollointhehouse.utils.crypt.RSA
import java.security.KeyPair
import javax.crypto.spec.SecretKeySpec
import kotlin.io.encoding.Base64

class ProxyAesKeyHandler(private val proxyKeyPair: KeyPair) : PacketHandler<PacketAESSendKey> {
    override suspend fun handle(context: PacketContext, packet: PacketAESSendKey): PacketAESSendKey {
        val rawAesKeyBytes = RSA.decrypt(packet.key, proxyKeyPair.private)

        val byteKey = Base64.decode(rawAesKeyBytes)
        val secretKey = SecretKeySpec(byteKey, "AES")

        context.session.chat.sharedAesKey = secretKey

        val realClientKey = context.session.chat.realClientPublicKey
            ?: error("Login packet wasn't intercepted before AES key arrived")
        packet.key = RSA.encrypt(rawAesKeyBytes, realClientKey)
        return packet
    }
}