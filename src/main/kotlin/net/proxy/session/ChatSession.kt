package dev.apollointhehouse.net.proxy.session

import java.security.Key
import java.security.PublicKey

data class ChatSession(
    var realClientPublicKey: PublicKey? = null,
    var sharedAesKey: Key? = null
)

