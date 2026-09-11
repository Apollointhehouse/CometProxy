package dev.apollointhehouse.network.proxy.config

import dev.apollointhehouse.network.crypto.RSA
import java.security.KeyPair

class ProxyConfig(
    val targetServer: String,
    val targetPort: Int = 25565,
    val hostPort: Int = 25565,
    val btaVersion: String = "8.0.1",
    val motd: String,
    val keyPair: KeyPair = RSA.generateKeyPair()
)