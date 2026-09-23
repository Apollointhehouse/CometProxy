package dev.apollointhehouse.network.proxy

import dev.apollointhehouse.network.packet.handshake.PacketDisconnect
import dev.apollointhehouse.network.packet.handshake.PacketPingHandshake
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import dev.apollointhehouse.network.proxy.connection.ConnectionContext
import dev.apollointhehouse.network.proxy.connection.ConnectionManager
import dev.apollointhehouse.network.proxy.connection.ProxyConnection
import dev.apollointhehouse.network.proxy.connection.toProxyConnection
import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import kotlinx.coroutines.*
import org.apache.logging.log4j.kotlin.logger

class Proxy(private val config: ProxyConfig) {
    private val log = logger()

    suspend fun start() = withContext(Dispatchers.Default) {
        val targetAddress = InetSocketAddress(config.targetServer, config.targetPort)
        val selectorManager = ActorSelectorManager(Dispatchers.IO)
        val serverConnManager = ConnectionManager(targetAddress, selectorManager)

        testServerConnection(serverConnManager)

        val proxySocket = aSocket(selectorManager)
            .tcp()
            .bind(port = config.hostPort) { reuseAddress = true }

        log.info("Comet-Proxy listening at ${proxySocket.localAddress}")

        try {
            while (true) {
                acceptConnection(proxySocket, serverConnManager)
            }
        } finally {
            withContext(NonCancellable) {
                proxySocket.close()
                selectorManager.close()
            }
        }
    }

    private suspend fun testServerConnection(conManager: ConnectionManager) {
        val con = conManager.getConnection() ?: error("Failed to connect to target server")

        con.use {
            con.writePacket(PacketPingHandshake(
                payload = 1u,
                identifier = 0u,
                pingHostString = "BTAPingHost",
                protocolVersion = 32769.toUByte(),
                hostname = "",
                port = 0,
            ))

            val pingResponse = con.receivePacket()
                ?: error("Failed to connect to target server")

            if (pingResponse !is PacketDisconnect) error("Server failed to respond to ping")
            if ("32769" !in pingResponse.reason) error("Server responded incorrectly to ping")
        }
    }

    private suspend fun CoroutineScope.acceptConnection(
        proxySocket: ServerSocket,
        serverConnManager: ConnectionManager
    ) {
        val client = proxySocket
            .accept()
            .connection()
            .toProxyConnection()

        log.info("Accepted $client")

        launch(CoroutineName("connection/$client")) {
            connection(serverConnManager, client)
        }
    }

    private suspend fun connection(
        serverConnManager: ConnectionManager,
        client: ProxyConnection
    ) {
        val server = serverConnManager.getConnection()

        if (server == null) {
            client.close()
            return
        }

        val ctx = ConnectionContext(client, server)

        try {
            Bridge(config, ctx).use {
                it.run()
            }
        } catch (e: BridgeClosedException) {
            log.info { "Connection closed due to: ${e.message}" }
        }
    }
}