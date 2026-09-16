package dev.apollointhehouse.network.proxy.session

import java.security.PublicKey
import javax.crypto.spec.SecretKeySpec
import kotlin.uuid.Uuid

sealed interface PlayerSession {
    val username: String
    val uuid: Uuid
    val clientPublicKey: PublicKey?
}

fun PreAuthSession.upgrade(entityID: Int) = AuthSession(
    username = username,
    uuid = uuid,
    clientPublicKey = clientPublicKey,
    entityID = entityID,
)

data class PreAuthSession(
    override val username: String,
    override val uuid: Uuid,
    override val clientPublicKey: PublicKey
) : PlayerSession

data class AuthSession(
    override val username: String,
    override val uuid: Uuid,
    override val clientPublicKey: PublicKey,
    val entityID: Int,
    var secretKey: SecretKeySpec? = null
) : PlayerSession