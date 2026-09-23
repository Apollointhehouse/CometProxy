package dev.apollointhehouse.ui.model

import androidx.compose.ui.graphics.ImageBitmap
import dev.apollointhehouse.network.packet.chat.PacketMessage
import dev.apollointhehouse.network.proxy.ProxyManager
import dev.apollointhehouse.network.proxy.connection.ConnectionContext
import dev.apollointhehouse.network.proxy.connection.ConnectionRegistry
import dev.apollointhehouse.ui.repo.PlayerHeadRepository
import dev.apollointhehouse.ui.state.ProxyUIState
import dev.apollointhehouse.ui.state.toProxyConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlin.uuid.Uuid

class AppViewModel(
    initialState: ProxyUIState,
    private val playerHeadRepo: PlayerHeadRepository,
    private val proxyManager: ProxyManager,
) {
    val state: StateFlow<ProxyUIState>
        field = MutableStateFlow(initialState)

    val connections: StateFlow<Set<ConnectionContext>> = ConnectionRegistry.connections
    val playerHeads: Map<Uuid, ImageBitmap> = playerHeadRepo.playerHeads

    fun startProxy() {
        state.update { it.copy(isRunning = true) }
        proxyManager.start(state.value.toProxyConfig())
    }

    fun stopProxy() {
        proxyManager.stop()
        state.update { it.copy(isRunning = false) }
    }

    fun selectConnection(connection: ConnectionContext?) =
        state.update { it.copy(selectedConnection = connection) }

    fun sendMessageToClient(ctx: ConnectionContext, message: String) =
        ctx.client.sendPacket(PacketMessage(message))

    fun sendMessageToServer(ctx: ConnectionContext, message: String) =
        ctx.server.sendPacket(PacketMessage(message))

    fun loadPlayerHead(uuid: Uuid) =
        playerHeadRepo.loadPlayerHead(uuid)
}
