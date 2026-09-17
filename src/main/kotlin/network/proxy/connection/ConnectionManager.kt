package dev.apollointhehouse.network.proxy.connection

import dev.apollointhehouse.network.extensions.close
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CancellationException
import org.apache.logging.log4j.kotlin.logger

class ConnectionManager(
    private val address: InetSocketAddress,
    private val selectorManager: SelectorManager,
) {
    private val log = logger()

    suspend fun getConnection(): Connection? {
        try {
            val socket = aSocket(selectorManager).tcp().connect(address) {
                keepAlive = true
            }
            return socket.connection()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.error(e) { "Failed to connect to $address" }
            return null
        }
    }
}

inline fun <T : Connection?, R> T.use(block: (T) -> R): R {
    try {
        return block(this)
    } finally {
        this?.close()
    }
}