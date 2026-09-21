package dev.apollointhehouse.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import dev.apollointhehouse.network.API
import dev.apollointhehouse.network.packet.chat.PacketMessage
import dev.apollointhehouse.network.proxy.ProxyManager
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import dev.apollointhehouse.network.proxy.connection.ConnectionContext
import dev.apollointhehouse.network.proxy.connection.ConnectionRegistry
import dev.apollointhehouse.ui.state.ProxyUIState
import dev.apollointhehouse.ui.state.toProxyConfig
import dev.apollointhehouse.ui.state.toProxyUIState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import java.util.concurrent.ConcurrentHashMap
import kotlin.uuid.Uuid

class AppViewModel(config: ProxyConfig, private val scope: CoroutineScope) {
    private val proxyManager = ProxyManager()
    private val loadingUuids = ConcurrentHashMap.newKeySet<Uuid>()
    private val heads = mutableStateMapOf<Uuid, ImageBitmap>()
    private val log = logger()

    val proxyState: StateFlow<ProxyUIState>
        field = MutableStateFlow(config.toProxyUIState())

    val connections: StateFlow<Set<ConnectionContext>> = ConnectionRegistry.connections
    var selectedConnection by mutableStateOf<ConnectionContext?>(null)

    fun startProxy() {
        proxyState.update { it.copy(isRunning = true) }
        proxyManager.start(proxyState.value.toProxyConfig())
    }

    fun stopProxy() {
        scope.launch {
            proxyManager.stop()
            proxyState.update { it.copy(isRunning = false) }
        }
    }

    fun sendMessageToClient(ctx: ConnectionContext, message: String) {
        val packet = PacketMessage(message = message)

        ctx.client.sendPacket(packet)
    }

    fun sendMessageToServer(ctx: ConnectionContext, message: String) {
        val packet = PacketMessage(message = message)

        ctx.server.sendPacket(packet)
    }

    fun fetchPlayerHead(uuid: Uuid): ImageBitmap? {
        val cached = heads[uuid]
        if (cached != null) return cached

        if (loadingUuids.add(uuid)) {
            scope.launch {
                try {
                    val img = API.fetchHead(uuid)
                    heads[uuid] = img
                } catch (e: Exception) {
                    log.error(e) { "Failed to fetch head" }
                } finally {
                    loadingUuids.remove(uuid)
                }
            }
        }
        return null
    }
}
