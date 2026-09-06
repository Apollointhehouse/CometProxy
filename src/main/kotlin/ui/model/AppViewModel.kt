package dev.apollointhehouse.ui.model

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import dev.apollointhehouse.net.proxy.ConnectionRegistry
import dev.apollointhehouse.net.proxy.ProxyManager
import dev.apollointhehouse.net.proxy.pipeline.ConnectionContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AppViewModel(private val scope: CoroutineScope) {
    private val proxyManager = ProxyManager()

    val connections: StateFlow<List<ConnectionContext>> = ConnectionRegistry.connections

    var running: Boolean by mutableStateOf(false)
        private set
    var targetHost: TextFieldValue by mutableStateOf(TextFieldValue("example.com"))
    var targetPort: TextFieldValue by mutableStateOf(TextFieldValue("25565"))
        private set

    var selectedConnection by mutableStateOf<ConnectionContext?>(null)
        private set

    fun selectConnection(connection: ConnectionContext) {
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
}
