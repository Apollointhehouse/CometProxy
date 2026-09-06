package dev.apollointhehouse.net.proxy.session

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PlayerSession {
    var chat: ChatSession = ChatSession()
    var entityId: Int? by mutableStateOf(null)
    var username: String? by mutableStateOf(null)
}