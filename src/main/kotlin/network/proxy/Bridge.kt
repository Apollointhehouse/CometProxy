package dev.apollointhehouse.network.proxy

import dev.apollointhehouse.network.packet.Packet
import dev.apollointhehouse.network.pipeline.PacketContext
import dev.apollointhehouse.network.pipeline.PacketPipeline
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import dev.apollointhehouse.network.proxy.connection.ConnectionContext
import dev.apollointhehouse.network.proxy.connection.ConnectionRegistry
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext

class Bridge(
    private val config: ProxyConfig,
    private val ctx: ConnectionContext,
) : AutoCloseable {
    suspend fun run(): Nothing = withContext(Dispatchers.IO) {
        val (client, server) = ctx
        val (clientIn = input, _ = output) = client
        val (serverIn = input, _ = output) = server

        val pipeline = PacketPipeline.create(config)

        try {
            val c2sContext = PacketContext(PacketContext.Direction.CLIENT_TO_SERVER, ctx)
            val s2cContext = PacketContext(PacketContext.Direction.SERVER_TO_CLIENT, ctx)

            val clientToServerJob = launch {
                while (true) {
                    val packet = Packet.readPacket(clientIn)
                        ?: throw BridgeClosedException("Client connection closed")
                    val result = pipeline.process(c2sContext, packet)

                    if (result != null) ctx.sendToServer(result)
                }
            }

            val serverToClientJob = launch {
                while (true) {
                    val packet = Packet.readPacket(serverIn)
                        ?: throw BridgeClosedException("Server connection closed")
                    val result = pipeline.process(s2cContext, packet)

                    if (result != null) ctx.sendToClient(result)
                }
            }

            select {
                clientToServerJob.onJoin { serverToClientJob.cancel() }
                serverToClientJob.onJoin { clientToServerJob.cancel() }
            }

            awaitCancellation()
        } finally {
            ConnectionRegistry.unregister(ctx)
        }
    }

    override fun close() {
        ctx.close()
    }
}

