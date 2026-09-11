package dev.apollointhehouse.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.input.TextFieldValue
import dev.apollointhehouse.network.API
import dev.apollointhehouse.network.packet.chat.PacketMessage
import dev.apollointhehouse.network.proxy.connection.ConnectionRegistry
import dev.apollointhehouse.network.proxy.ProxyManager
import dev.apollointhehouse.network.proxy.connection.ConnectionContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.apache.logging.log4j.kotlin.logger
import java.util.concurrent.ConcurrentHashMap
import kotlin.uuid.Uuid

class AppViewModel(private val scope: CoroutineScope) {
    private val proxyManager = ProxyManager()
    private val log = logger()

    val connections: StateFlow<List<ConnectionContext>> = ConnectionRegistry.connections

    var running: Boolean by mutableStateOf(false)
        private set
    var targetHost: TextFieldValue by mutableStateOf(TextFieldValue("example.com"))
    var targetPort: TextFieldValue by mutableStateOf(TextFieldValue("25565"))
        private set

    var selectedConnection by mutableStateOf<ConnectionContext?>(null)
        private set

    val heads = mutableStateMapOf<Uuid, ImageBitmap>()

    private val loadingUuids = ConcurrentHashMap.newKeySet<Uuid>()

    fun selectConnection(connection: ConnectionContext?) {
        selectedConnection = connection
    }

    fun startProxy() {
        val host = targetHost.text
        val port = targetPort.text.toIntOrNull() ?: 25565

        if (host.isNotBlank() && targetPort.text.isNotBlank()) {
            running = true
            proxyManager.start(host, port)
        }
    }

    fun stopProxy() {
        scope.launch {
            proxyManager.stop()
            running = false
        }
    }

    fun updatePort(newValue: TextFieldValue) {
        if (newValue.text.all { it.isDigit() }) {
            targetPort = newValue
        }
    }

    fun sendMessageToClient(ctx: ConnectionContext, message: String) {
        val packet = PacketMessage(message = message)

        ctx.sendToClient(packet)
    }

    fun sendMessageToServer(ctx: ConnectionContext, message: String) {
        val packet = PacketMessage(message = message)

        ctx.sendToServer(packet)
    }

    fun getHead(uuid: Uuid): ImageBitmap? {
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
