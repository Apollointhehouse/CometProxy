package dev.apollointhehouse.proxy

import dev.apollointhehouse.Global.proxyKeyPair
import dev.apollointhehouse.SocketPool
import dev.apollointhehouse.utils.crypt.RSA
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger
import java.net.InetAddress

class Proxy {
    val log = logger()

    suspend fun runProxy(
        targetServer: String,
        targetPort: Int
    ) = withContext(Dispatchers.IO) {
        val port = 25565
        val resolvedIp = runCatching { InetAddress.getByName(targetServer) }
            .onFailure { log.error(it.toString()) }
            .getOrNull()?.hostAddress ?: return@withContext

        val targetAddress = InetSocketAddress(resolvedIp, targetPort)

        proxyKeyPair = RSA.generateKeyPair()

        val selectorManager = ActorSelectorManager(Dispatchers.IO)

        val mitmSocket = aSocket(selectorManager).tcp().bind(port = port) {
            reuseAddress = true
        }
        log.info("Comet-Proxy listening at ${mitmSocket.localAddress}")

        val serverSocketPool = SocketPool(targetAddress, 3, selectorManager)
        serverSocketPool.init()

        try {
            broadcast(mitmSocket, serverSocketPool)
        } finally {
            withContext(NonCancellable) {
                mitmSocket.close()
                serverSocketPool.close()
                selectorManager.close()
                log.info("Proxy Stopped")
            }
        }
    }

    private suspend fun broadcast(
        mitmSocket: ServerSocket,
        serverSocketPool: SocketPool
    ) = withContext(Dispatchers.IO) {
        while (true) {
            val clientSocket = mitmSocket.accept()
            log.info("Accepted ${clientSocket.remoteAddress}")

            launch(Dispatchers.IO) {
                val serverSocket = serverSocketPool.getSocket() ?: return@launch

                try {
                    Bridge(clientSocket, serverSocket).run()
                } catch (e: Exception) {
                    log.error("Error bridging connection", e)
                } finally {
                    serverSocket.close()
                    clientSocket.close()
                }
            }
        }
    }
}