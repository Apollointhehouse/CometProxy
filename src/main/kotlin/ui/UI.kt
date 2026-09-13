package dev.apollointhehouse.ui

import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import dev.apollointhehouse.ui.model.AppViewModel
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme

fun UI(proxyConfig: ProxyConfig) = application {
    IntUiTheme(isDark = true) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CometProxy",
        ) {
            val coroutineScope = rememberCoroutineScope()
            val viewModel = remember { AppViewModel(proxyConfig, coroutineScope) }

            Home(viewModel)
        }
    }
}