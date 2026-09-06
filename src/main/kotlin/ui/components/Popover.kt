package dev.apollointhehouse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import org.jetbrains.jewel.foundation.theme.JewelTheme

@Composable
fun PopoverAnchor(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    alignment: Alignment = Alignment.TopEnd,
    verticalOffset: Dp = 28.dp,
    popupContent: @Composable () -> Unit,
    anchor: @Composable () -> Unit,
) {
    Box(modifier) {
        anchor()

        if (expanded) {
            Popup(
                alignment = alignment,
                offset = IntOffset(0, with(LocalDensity.current) { verticalOffset.roundToPx() }),
                onDismissRequest = onDismissRequest,
                properties = PopupProperties(focusable = true),
            ) {
                popupContent()
            }
        }
    }
}

@Composable
fun PopoverPanel(
    modifier: Modifier = Modifier,
    width: Dp = 240.dp,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .width(width)
            .clip(RoundedCornerShape(3.dp))
            .background(JewelTheme.globalColors.panelBackground)
            .border(1.dp, Color(0xFF3C3F41), RoundedCornerShape(3.dp))
            .padding(10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        content = content,
    )
}