package dev.apollointhehouse.network.crypto

import java.nio.charset.StandardCharsets
import java.security.Key
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.spec.SecretKeySpec

object AES {
    var keyChain: MutableMap<String, Key> = mutableMapOf()
    var clientKeyChain: Key? = null

    fun generateKey(): Key {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(256)
        return keyGenerator.generateKey()
    }

    fun encrypt(plainText: String, key: Key?): String? {
        val encryptCipher = Cipher.getInstance("AES")
        encryptCipher.init(1, key)
        val cipherText = encryptCipher.doFinal(plainText.toByteArray(StandardCharsets.UTF_8))
        return Base64.getEncoder().encodeToString(cipherText)
    }

    fun decrypt(cipherText: String?, key: Key?): String {
        val bytes = Base64.getDecoder().decode(cipherText)
        val decryptCipher = Cipher.getInstance("AES")
        decryptCipher.init(2, key)
        return String(decryptCipher.doFinal(bytes), StandardCharsets.UTF_8)
    }

    fun getKey(key: Key): String? {
        return Base64.getEncoder().encodeToString(key.encoded)
    }

    fun getKey(key: String?): Key {
        val byteKey = Base64.getDecoder().decode(key)
        val secretKey = SecretKeySpec(byteKey, "AES")
        return secretKey
    }
}