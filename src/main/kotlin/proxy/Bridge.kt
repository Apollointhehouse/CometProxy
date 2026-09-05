package dev.apollointhehouse.proxy

import dev.apollointhehouse.Global.proxyKeyPair
import dev.apollointhehouse.packet.Packet
import dev.apollointhehouse.proxy.handlers.ProxyAesKeyHandler
import dev.apollointhehouse.proxy.handlers.ProxyLoginHandler
import dev.apollointhehouse.proxy.pipeline.NetContext
import dev.apollointhehouse.proxy.pipeline.PacketContext
import dev.apollointhehouse.proxy.pipeline.PacketPipeline
import dev.apollointhehouse.proxy.session.PlayerSession
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
        try {
            val clientIn = clientSocket.openReadChannel()
            val clientOut = clientSocket.openWriteChannel()

            val serverIn = serverSocket.openReadChannel()
            val serverOut = serverSocket.openWriteChannel()

            val pipeline = buildPipeline()
            val session = PlayerSession()

            val netContext = NetContext(serverOut, clientOut, session)

            val c2sContext = PacketContext(PacketContext.Direction.CLIENT_TO_SERVER, netContext)
            val s2cContext = PacketContext(PacketContext.Direction.SERVER_TO_CLIENT, netContext)

            val clientToServerJob = launch {
                try {
                    while (true) {
                        val packet = Packet.readPacket(clientIn) ?: break
                        val result = pipeline.process(c2sContext, packet)
                        if (result != null) netContext.sendToServer(result)
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
                        if (result != null) netContext.sendToClient(result)
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
            clientSocket.close()
            serverSocket.close()
        }
    }

    private fun buildPipeline(): PacketPipeline {
        val pipeline = PacketPipeline()

        pipeline += ProxyLoginHandler(proxyKeyPair)
        pipeline += ProxyAesKeyHandler(proxyKeyPair)

        return pipeline
    }
}

