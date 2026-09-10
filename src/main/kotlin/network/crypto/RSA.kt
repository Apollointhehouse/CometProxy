package dev.apollointhehouse.network.crypto

import java.nio.charset.StandardCharsets
import java.security.*
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import kotlin.io.encoding.Base64

object RSA {
    fun generateKeyPair(): KeyPair {
        val generator = KeyPairGenerator.getInstance("RSA")
        generator.initialize(2048, SecureRandom())
        val pair = generator.generateKeyPair()
        return pair
    }

    fun encrypt(plainText: String, publicKey: PublicKey): String {
        val encryptCipher = Cipher.getInstance("RSA")
        encryptCipher.init(1, publicKey)
        val cipherText = encryptCipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        return Base64.encode(cipherText)
    }

    fun decrypt(cipherText: String, privateKey: PrivateKey): String {
        val bytes = Base64.decode(cipherText)
        val decryptCipher = Cipher.getInstance("RSA")
        decryptCipher.init(2, privateKey)
        return String(decryptCipher.doFinal(bytes), StandardCharsets.UTF_8)
    }

    fun getPublicKey(publicKey: PublicKey): String {
        return Base64.encode(publicKey.encoded)
    }

    fun getPublicKey(key: String): PublicKey {
        val byteKey = Base64.decode(key)
        val x509publicKey = X509EncodedKeySpec(byteKey)
        val kf = KeyFactory.getInstance("RSA")
        return kf.generatePublic(x509publicKey)
    }
}