package dev.apollointhehouse.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.jewel.ui.component.GroupHeader
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.VerticallyScrollableContainer

@Composable
fun ProxyLog(logLines: SnapshotStateList<String>) {
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