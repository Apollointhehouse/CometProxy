package dev.apollointhehouse

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.apollointhehouse.ui.App
import dev.apollointhehouse.ui.model.AppViewModel
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme

fun main() = application {
    IntUiTheme(isDark = true) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Comet-Proxy",
        ) {
            val coroutineScope = rememberCoroutineScope()
            val viewModel = remember { AppViewModel(coroutineScope) }

            App(viewModel)
        }
    }
}