@file:OptIn(ExperimentalJewelApi::class)
package dev.apollointhehouse.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import dev.apollointhehouse.net.proxy.pipeline.ConnectionContext
import dev.apollointhehouse.ui.components.ActionSection
import dev.apollointhehouse.ui.components.PopoverPanel
import dev.apollointhehouse.ui.model.AppViewModel
import org.jetbrains.jewel.foundation.ExperimentalJewelApi
import org.jetbrains.jewel.ui.component.DefaultButton
import org.jetbrains.jewel.ui.component.Text
import org.jetbrains.jewel.ui.component.TextField

@Composable
fun ConnectionActionsPopup(
    ctx: ConnectionContext,
    viewModel: AppViewModel,
) {
    var messageText by remember(ctx.id) { mutableStateOf(TextFieldValue("")) }

    PopoverPanel {
        ActionSection("Send Message") {
            TextField(
                value = messageText,
                onValueChange = { messageText = it },
                placeholder = { Text("Message…") },
                modifier = Modifier.fillMaxWidth(),
            )

            DefaultButton(
                enabled = messageText.text.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.sendMessageToClient(ctx, messageText.text)
                    messageText = TextFieldValue("")
                },
            ) { Text("Send To Client") }

            DefaultButton(
                enabled = messageText.text.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    viewModel.sendMessageToServer(ctx, messageText.text)
                    messageText = TextFieldValue("")
                },
            ) { Text("Send To Server") }
        }
    }
}