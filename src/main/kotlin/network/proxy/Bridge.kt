package dev.apollointhehouse.network.proxy

import dev.apollointhehouse.network.extensions.close
import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.packet.handshake.PacketDisconnect
import dev.apollointhehouse.network.packet.handshake.PacketPingHandshake
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import dev.apollointhehouse.network.proxy.connection.ConnectionRegistry
import dev.apollointhehouse.network.proxy.handlers.ChatMessageHandler
import dev.apollointhehouse.network.proxy.handlers.ProxyAesKeyHandler
import dev.apollointhehouse.network.proxy.handlers.ProxyLoginHandler
import dev.apollointhehouse.network.proxy.connection.ConnectionContext
import dev.apollointhehouse.network.proxy.pipeline.PacketContext
import dev.apollointhehouse.network.proxy.pipeline.PacketPipeline
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger

class Bridge(
    val config: ProxyConfig,
    val client: Connection,
    val server: Connection
) : AutoCloseable {
    private val log = logger()

    suspend fun run() = withContext(Dispatchers.IO) {
        val (clientIn = input, _ = output) = client
        val (serverIn = input, _ = output) = server

        val ctx = ConnectionContext(
            clientConn = client,
            serverConn = server
        )

        val pipeline = buildPipeline()

        try {
            val c2sContext = PacketContext(
                PacketContext.Direction.CLIENT_TO_SERVER,
                ctx
            )
            val s2cContext = PacketContext(
                PacketContext.Direction.SERVER_TO_CLIENT,
                ctx
            )

            val clientToServerJob = launch {
                try {
                    while (true) {
                        val packet = Packet.readPacket(clientIn) ?: break
                        val result = pipeline.process(c2sContext, packet)

                        if (result != null) ctx.sendToServer(result)
                    }
                }
                catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    log.error(e) { "Client disconnected or error: ${e.message}" }
                }
            }

            val serverToClientJob = launch {
                try {
                    while (true) {
                        val packet = Packet.readPacket(serverIn) ?: break
                        val result = pipeline.process(s2cContext, packet)

                        if (result != null) ctx.sendToClient(result)
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    log.error(e) { "Server disconnected or error: ${e.message}" }
                }
            }

            select {
                clientToServerJob.onJoin { serverToClientJob.cancel() }
                serverToClientJob.onJoin { clientToServerJob.cancel() }
            }
        }  catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            log.error(e) { "Failed to establish backend proxy connection: ${e.message}" }
        } finally {
            ConnectionRegistry.unregister(ctx)
            client.close()
            server.close()
            ctx.close()
        }
    }

    private fun buildPipeline(): PacketPipeline {
        val pipeline = PacketPipeline()

        pipeline += ProxyLoginHandler(config.keyPair)
        pipeline += ProxyAesKeyHandler(config.keyPair)
        pipeline += ChatMessageHandler()
        pipeline += { ctx, _: PacketPingHandshake ->
            log.info { "Client Ping!" }

            val con = ctx.connection
            con.sendToClient(PacketDisconnect(reason = "§1\u000032769\u0000${config.btaVersion}\u0000${config.motd}\u00000\u0000100\u0000\u0000"))

            null
        }

        return pipeline
    }

    override fun close() {
        client.close()
        server.close()
    }
}

