package dev.apollointhehouse.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.apollointhehouse.ui.model.AppViewModel
import org.jetbrains.jewel.ui.component.GroupHeader
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun ProxyTargetInput(viewModel: AppViewModel) {
    GroupHeader("Target Server")
    Spacer(Modifier.height(8.dp))

    val proxyState by viewModel.proxyState.collectAsState()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextField(
            state = proxyState.targetServer,
            enabled = !proxyState.isRunning,
            placeholder = { Text("Host IP/address") },
            modifier = Modifier.weight(1f),
        )
        TextField(
            state = proxyState.targetPort,
            enabled = !proxyState.isRunning,
            placeholder = { Text("Port") },
            modifier = Modifier.width(100.dp),
        )
    }
}