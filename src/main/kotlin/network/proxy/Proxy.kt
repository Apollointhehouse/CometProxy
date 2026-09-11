package dev.apollointhehouse.network.proxy

import dev.apollointhehouse.network.extensions.close
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import dev.apollointhehouse.network.proxy.connection.ConnectionManager
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger

class Proxy(val config: ProxyConfig) {
    private val log = logger()

    suspend fun start() = withContext(Dispatchers.IO) {
        val targetAddress = InetSocketAddress(config.targetServer, config.targetPort)

        val selectorManager = ActorSelectorManager(Dispatchers.IO)

        val serverConnManager = ConnectionManager(targetAddress, selectorManager)

        val proxySocket = aSocket(selectorManager).tcp().bind(port = config.hostPort) {
            reuseAddress = true
        }
        log.info("Comet-Proxy listening at ${proxySocket.localAddress}")

        try {
            while (true) {
                acceptConnection(proxySocket, serverConnManager)
            }
        } finally {
            withContext(NonCancellable) {
                proxySocket.close()
                selectorManager.close()
                log.info("Proxy Stopped")
            }
        }
    }

    private suspend fun CoroutineScope.acceptConnection(
        proxySocket: ServerSocket,
        serverConnManager: ConnectionManager
    ) {
        val clientConn = proxySocket.accept().connection()
        log.info("Accepted ${clientConn.socket.remoteAddress}")

        launch(Dispatchers.IO) {
            val serverConn = serverConnManager.getConnection() ?: return@launch
            val bridge = Bridge(config, clientConn, serverConn)

            try {
                bridge.run()
            } catch (e: Exception) {
                log.error("Error bridging connection", e)
            } finally {
                bridge.close()
                serverConn.close()
                clientConn.close()
            }
        }
    }
}