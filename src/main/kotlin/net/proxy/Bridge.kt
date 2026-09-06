package dev.apollointhehouse.net.proxy

import dev.apollointhehouse.Global.proxyKeyPair
import dev.apollointhehouse.net.packet.Packet
import dev.apollointhehouse.net.proxy.handlers.ChatMessageHandler
import dev.apollointhehouse.net.proxy.handlers.ProxyAesKeyHandler
import dev.apollointhehouse.net.proxy.handlers.ProxyLoginHandler
import dev.apollointhehouse.net.proxy.pipeline.ConnectionContext
import dev.apollointhehouse.net.proxy.pipeline.PacketContext
import dev.apollointhehouse.net.proxy.pipeline.PacketPipeline
import dev.apollointhehouse.net.proxy.session.PlayerSession
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext
import org.apache.logging.log4j.kotlin.logger

class Bridge(
    val clientSocket: Socket,
    val serverSocket: Socket
) {
    private val log = logger()

    suspend fun run() = withContext(Dispatchers.IO) {
        val clientIn = clientSocket.openReadChannel()
        val clientOut = clientSocket.openWriteChannel()

        val serverIn = serverSocket.openReadChannel()
        val serverOut = serverSocket.openWriteChannel()
        val session = PlayerSession()

        val ctx =
            ConnectionContext(serverOut, clientOut, session)

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
            clientSocket.close()
            serverSocket.close()
        }
    }

    private fun buildPipeline(): PacketPipeline {
        val pipeline = PacketPipeline()

        pipeline += ProxyLoginHandler(proxyKeyPair)
        pipeline += ProxyAesKeyHandler(proxyKeyPair)
        pipeline += ChatMessageHandler()

        return pipeline
    }
}

