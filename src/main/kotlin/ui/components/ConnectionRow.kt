package dev.apollointhehouse.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.typography

@Composable
fun ConnectionRow(
    viewModel: AppViewModel,
    ctx: ConnectionContext,
    isSelected: Boolean,
    onDismissRequest: () -> Unit,
    onClick: () -> Unit,
) {
    val session = ctx.session ?: return
    val name = session.username
    val uuid = session.uuid
    val headImg = viewModel.getHead(uuid)

    PopoverAnchor(
        expanded = isSelected,
        onDismissRequest = onDismissRequest,
        modifier = Modifier.fillMaxWidth(),
        alignment = Alignment.TopStart,
        verticalOffset = 46.dp,
        popupContent = {
            ConnectionActionsPopup(
                ctx = ctx,
                viewModel = viewModel
            )
        }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(3.dp))
                .background(if (isSelected) Color(0xFF2B5278) else Color.Transparent)
                .clickable {
                    if (!isSelected) onClick()
                }
                .padding(horizontal = 10.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier
                    .size(28.dp)
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