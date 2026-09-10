package dev.apollointhehouse.network.proxy.session

import kotlin.uuid.Uuid

class PlayerSession(
    var chat: ChatSession = ChatSession(),
    var username: String,
    var uuid: Uuid
) {
    var entityId: Int? = null
}