@file:OptIn(ExperimentalJewelApi::class)
package dev.apollointhehouse.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import dev.apollointhehouse.net.proxy.pipeline.ConnectionContext
import dev.apollointhehouse.ui.model.AppViewModel
import dev.apollointhehouse.utils.GuiLogBus
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.Orientation
import org.jetbrains.jewel.ui.component.*
import org.jetbrains.jewel.ui.typography

@Composable
fun App(viewModel: AppViewModel) {
    val connections by viewModel.connections.collectAsState()
    val logLines = remember { mutableStateListOf<String>() }

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
        Row(verticalAlignment = Alignment.CenterVertically) {
            StatusDot(viewModel.running)
            Spacer(Modifier.width(8.dp))
            Text(
                text = "Comet-Proxy",
                style = JewelTheme.typography.h2TextStyle,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = if (viewModel.running) "Running" else "Stopped",
                color = if (viewModel.running) Color(0xFF6BC46D) else JewelTheme.globalColors.text.disabled,
                style = JewelTheme.typography.medium,
            )
        }

        Spacer(Modifier.height(16.dp))
        Divider(orientation = Orientation.Horizontal)
        Spacer(Modifier.height(16.dp))

        TargetInput(viewModel)

        Spacer(Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            DefaultButton(
                onClick = { viewModel.startProxy() },
                enabled = !viewModel.running && viewModel.targetHost.text.isNotBlank() && viewModel.targetPort.text.isNotBlank(),
            ) {
                Text("Start Proxy")
            }

            OutlinedButton(
                onClick = { viewModel.stopProxy() },
                enabled = viewModel.running,
            ) {
                Text("Stop")
            }

            Spacer(Modifier.weight(1f))

            OutlinedButton(onClick = { logLines.clear() }) {
                Text("Clear Log")
            }
        }

        Spacer(Modifier.height(20.dp))
        Divider(orientation = Orientation.Horizontal)
        Spacer(Modifier.height(12.dp))

        ConnectionsPanel(
            viewModel = viewModel,
            connections = connections,
            selected = viewModel.selectedConnection,
            onSelect = { viewModel.selectConnection(it) },
        )

        Spacer(Modifier.height(20.dp))
        Divider(orientation = Orientation.Horizontal)
        Spacer(Modifier.height(12.dp))

        ConsoleLog(logLines)
    }
}

@Composable
private fun TargetInput(viewModel: AppViewModel) {
    GroupHeader("Target Server")
    Spacer(Modifier.height(8.dp))

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        TextField(
            value = viewModel.targetHost,
            onValueChange = { viewModel.targetHost = it },
            enabled = !viewModel.running,
            placeholder = { Text("Host IP/address") },
            modifier = Modifier.weight(1f),
        )
        TextField(
            value = viewModel.targetPort,
            onValueChange = { if (it.text.all(Char::isDigit)) viewModel.updatePort(it) },
            enabled = !viewModel.running,
            placeholder = { Text("Port") },
            modifier = Modifier.width(100.dp),
        )
    }
}

@Composable
private fun ConnectionsPanel(
    viewModel: AppViewModel,
    connections: List<ConnectionContext>,
    selected: ConnectionContext?,
    onSelect: (ConnectionContext) -> Unit,
) {
    GroupHeader("Connections")
    Spacer(Modifier.height(8.dp))

    Box(
        Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFF1E1F22))
    ) {
        if (connections.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    text = "No players connected",
                    color = JewelTheme.globalColors.text.disabled,
                    style = JewelTheme.typography.medium,
                )
            }
            return
        }

        VerticallyScrollableContainer(
            modifier = Modifier.fillMaxSize(),
            scrollState = rememberScrollState(),
        ) {
            Column {
                for (ctx in connections) {
                    ConnectionRow(
                        viewModel = viewModel,
                        ctx = ctx,
                        isSelected = ctx.id == selected?.id,
                        onClick = { onSelect(ctx) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ConnectionRow(
    viewModel: AppViewModel,
    ctx: ConnectionContext,
    isSelected: Boolean,
    onClick: () -> Unit,
) {
    val session = ctx.session ?: return
    val name = session.username
    val uuid = session.uuid
    val headImg = viewModel.getHead(uuid)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(4.dp))
            .background(if (isSelected) Color(0xFF2B5278) else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(avatarColor(name)),
            contentAlignment = Alignment.Center,
        ) {
            if (headImg != null) {
                Image(bitmap = headImg, contentDescription = null)
            } else {
                Text(
                    text = name.firstOrNull()?.uppercase() ?: "?",
                    color = Color.White,
                    style = JewelTheme.typography.medium,
                )
            }
        }

        Spacer(Modifier.width(10.dp))

        Column(Modifier.weight(1f)) {
            Text(
                text = name,
                color = JewelTheme.globalColors.text.normal,
                style = JewelTheme.typography.medium,
            )
            Text(
                text = "Entity ID: ${session.entityId}",
                color = if (!isSelected) JewelTheme.globalColors.text.disabled else JewelTheme.globalColors.text.normal,
                style = JewelTheme.typography.small,
            )
            Text(
                text = "UUID: ${session.uuid}",
                color = if (!isSelected) JewelTheme.globalColors.text.disabled else JewelTheme.globalColors.text.normal,
                style = JewelTheme.typography.small,
            )
        }

        if (isSelected) {
            Box(
                Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF6BC46D))
            )
        }
    }
}

private fun avatarColor(seed: String): Color {
    val palette = listOf(
        Color(0xFF5865F2), Color(0xFF57A64A), Color(0xFFD9822B),
        Color(0xFFB85C8A), Color(0xFF3C9EA6), Color(0xFF9575CD),
    )
    return palette[Math.floorMod(seed.hashCode(), palette.size)]
}

@Composable
private fun ConsoleLog(logLines: SnapshotStateList<String>) {
    GroupHeader("Log")
    Spacer(Modifier.height(8.dp))

    Box(
        Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(3.dp))
            .background(Color(0xFF1E1F22))
            .padding(12.dp)
    ) {
        val listState = rememberLazyListState()

        LaunchedEffect(logLines.size) {
            if (logLines.isNotEmpty()) listState.animateScrollToItem(logLines.lastIndex)
        }

        VerticallyScrollableContainer(
            modifier = Modifier.fillMaxSize(),
            scrollState = rememberScrollState()
        ) {
            Column {
                logLines.forEach { line ->
                    Text(
                        text = line,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 12.sp,
                        color = Color(0xFFD4D4D4),
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusDot(running: Boolean) {
    Box(
        Modifier
            .size(10.dp)
            .clip(CircleShape)
            .background(if (running) Color(0xFF6BC46D) else Color(0xFF8A8A8A))
    )
}