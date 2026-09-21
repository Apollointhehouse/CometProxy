package dev.apollointhehouse.network.proxy.connection

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CancellationException
import org.apache.logging.log4j.kotlin.logger

class ConnectionManager(
    private val address: InetSocketAddress,
    private val selectorManager: SelectorManager,
) {
    private val log = logger()

    suspend fun getConnection(): ProxyConnection? {
        try {
            val socket = aSocket(selectorManager).tcp().connect(address) {
                keepAlive = true
            }
            return socket.connection().toProxyConnection()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.error(e) { "Failed to connect to $address" }
            return null
        }
    }
}