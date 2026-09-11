package dev.apollointhehouse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.apollointhehouse.network.proxy.connection.ConnectionContext
import dev.apollointhehouse.ui.model.AppViewModel
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.ui.component.GroupHeader
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.VerticallyScrollableContainer
import org.jetbrains.jewel.ui.typography

@Composable
fun ConnectionsPanel(
    viewModel: AppViewModel,
    connections: List<ConnectionContext>,
    selected: ConnectionContext?,
    onDismissRequest: () -> Unit,
    onSelect: (ConnectionContext?) -> Unit,
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
            modifier = Modifier
                .fillMaxSize(),
            scrollState = rememberScrollState(),
        ) {
            Column {
                for (ctx in connections) {
                    ConnectionRow(
                        viewModel = viewModel,
                        ctx = ctx,
                        isSelected = ctx.id == selected?.id,
                        onDismissRequest = onDismissRequest,
                        onClick = { onSelect(ctx) },
                    )
                }
            }
        }
    }
}