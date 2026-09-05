package dev.apollointhehouse.proxy.session

import java.security.Key
import java.security.PublicKey

class ChatSession {
    var realClientPublicKey: PublicKey? = null
    var sharedAesKey: Key? = null
}

