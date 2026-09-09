package dev.apollointhehouse.net.proxy

import dev.apollointhehouse.data.ProxyConfig
import dev.apollointhehouse.utils.extensions.close
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger
import java.net.InetAddress

class Proxy(val config: ProxyConfig) {
    private val log = logger()

    suspend fun start() = withContext(Dispatchers.IO) {
        val resolvedIp = runCatching { InetAddress.getByName(config.targetServer) }
            .onFailure { log.error(it.toString()) }
            .getOrNull()?.hostAddress ?: return@withContext

        val targetAddress = InetSocketAddress(resolvedIp, config.targetPort)

        val selectorManager = ActorSelectorManager(Dispatchers.IO)

        val proxySocket = aSocket(selectorManager).tcp().bind(port = config.hostPort) {
            reuseAddress = true
        }
        log.info("Comet-Proxy listening at ${proxySocket.localAddress}")

        val serverConnPool = ConnectionPool(targetAddress, config.poolSize, selectorManager)
        serverConnPool.init()

        try {
            acceptConnections(proxySocket, serverConnPool)
        } finally {
            withContext(NonCancellable) {
                proxySocket.close()
                serverConnPool.close()
                selectorManager.close()
                log.info("Proxy Stopped")
            }
        }
    }

    private suspend fun acceptConnections(
        proxySocket: ServerSocket,
        serverConnPool: ConnectionPool
    ) = withContext(Dispatchers.IO) {
        while (true) {
            val clientSocket = proxySocket.accept()
            log.info("Accepted ${clientSocket.remoteAddress}")

            val clientConn = clientSocket.connection()

            launch(Dispatchers.IO) {
                val serverConn = serverConnPool.getConnection() ?: return@launch

                try {
                    Bridge(config, clientConn, serverConn).run()
                } catch (e: Exception) {
                    log.error("Error bridging connection", e)
                } finally {
                    serverConn.close()
                    clientConn.close()
                }
            }
        }
    }
}