package dev.apollointhehouse

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.apollointhehouse.ui.App
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme

fun main() = application {
    IntUiTheme(isDark = true) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Comet-Proxy",
        ) {
            App()
        }
    }
}