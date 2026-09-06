package dev.apollointhehouse.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.apollointhehouse.ui.model.AppViewModel
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.GroupHeader
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@OptIn(ExperimentalJewelApi::class)
@Composable
fun ProxyTargetInput(viewModel: AppViewModel) {
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