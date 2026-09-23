package dev.apollointhehouse.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.apollointhehouse.ui.components.ConnectionsPanel
import dev.apollointhehouse.ui.components.ProxyActions
import dev.apollointhehouse.ui.components.ProxyLog
import dev.apollointhehouse.ui.components.ProxyTargetInput
import dev.apollointhehouse.ui.logging.GuiLogBus
import dev.apollointhehouse.ui.model.AppViewModel
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.Divider

@Composable
fun Home(viewModel: AppViewModel) {
    val connections by viewModel.connections.collectAsState()
    val logLines = remember { mutableStateListOf<String>() }
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        GuiLogBus.events.collect { line ->
            logLines.add(line)
            if (logLines.size > 1000) logLines.removeAt(0)
        }
    }

    Column(
        Modifier
            .fillMaxSize()
            .background(JewelTheme.globalColors.panelBackground)
            .padding(20.dp)
    ) {
        ProxyTargetInput(viewModel)

        Spacer(Modifier.height(16.dp))

        ProxyActions(viewModel, logLines)

        Spacer(Modifier.height(20.dp))
        Divider(orientation = Orientation.Horizontal)
        Spacer(Modifier.height(12.dp))

        ConnectionsPanel(
            viewModel = viewModel,
            connections = connections,
            selected = state.selectedConnection,
            onDismissRequest = { viewModel.selectConnection(null) },
            onSelect = { viewModel.selectConnection(it) },
        )

        Spacer(Modifier.height(20.dp))
        Divider(orientation = Orientation.Horizontal)
        Spacer(Modifier.height(12.dp))

        ProxyLog(logLines)
    }
}