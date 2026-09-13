package dev.apollointhehouse.ui.state

import androidx.compose.foundation.text.input.TextFieldState
import dev.apollointhehouse.network.proxy.config.ProxyConfig

data class ProxyUIState(
    val targetServer: TextFieldState,
    val targetPort: TextFieldState,
    val hostPort: TextFieldState,
    val motd: TextFieldState,
)

fun ProxyUIState.toProxyConfig() = ProxyConfig(
    targetServer = targetServer.text.toString(),
    targetPort = targetPort.text.toString().toInt(),
    hostPort = hostPort.text.toString().toInt(),
    motd = motd.text.toString()
)

fun ProxyConfig.toProxyUIState() = ProxyUIState(
    targetServer = TextFieldState(targetServer),
    targetPort = TextFieldState(targetPort.toString()),
    hostPort = TextFieldState(hostPort.toString()),
    motd = TextFieldState(motd),
)