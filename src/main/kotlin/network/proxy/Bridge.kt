package dev.apollointhehouse.network.proxy

import dev.apollointhehouse.network.pipeline.PacketContext
import dev.apollointhehouse.network.pipeline.PacketPipeline
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import dev.apollointhehouse.network.proxy.connection.ConnectionContext
import dev.apollointhehouse.network.proxy.connection.ConnectionRegistry
import dev.apollointhehouse.network.proxy.connection.ProxyConnection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import kotlinx.coroutines.withContext

class Bridge(
    private val config: ProxyConfig,
    private val ctx: ConnectionContext,
) : AutoCloseable {
    suspend fun run(): Nothing = withContext(Dispatchers.Default) {
        val (client, server) = ctx
        val pipeline = PacketPipeline.create(config)

        try {
            val c2sContext = PacketContext(PacketContext.Direction.CLIENT_TO_SERVER, ctx)
            val s2cContext = PacketContext(PacketContext.Direction.SERVER_TO_CLIENT, ctx)

            val clientToServerJob = launch {
                forwardingJob(client, server, pipeline, c2sContext)
            }

            val serverToClientJob = launch {
                forwardingJob(server, client, pipeline, s2cContext)
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

    private suspend fun forwardingJob(
        source: ProxyConnection,
        sink: ProxyConnection,
        pipeline: PacketPipeline,
        context: PacketContext
    ) {
        while (true) {
            val packet = source.readPacket()
                ?: throw BridgeClosedException("${context.direction} connection closed")
            val result = pipeline.process(context, packet)

            if (result != null) sink.queuePacket(result)
        }
    }

    override fun close() {
        ctx.close()
    }
}

