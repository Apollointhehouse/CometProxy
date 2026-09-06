package dev.apollointhehouse.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.apollointhehouse.ui.model.AppViewModel
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.OutlinedButton
import org.jetbrains.jewel.ui.component.Text

@Composable
fun ProxyActions(
    viewModel: AppViewModel,
    logLines: SnapshotStateList<String>
) {
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
}