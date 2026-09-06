package dev.apollointhehouse.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import dev.apollointhehouse.net.proxy.pipeline.ConnectionContext
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
            .clickable { onSelect(null) },
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
                .fillMaxSize()
                .clickable { onSelect(null) },
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