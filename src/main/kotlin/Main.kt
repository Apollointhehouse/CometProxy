package dev.apollointhehouse

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.LoggerContext
import dev.apollointhehouse.ui.Home
import dev.apollointhehouse.ui.model.AppViewModel
import dev.apollointhehouse.utils.JvmWarmup
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory



fun main() = application {
    LaunchedEffect(Unit) {
        val isDebugging = ManagementFactory.getRuntimeMXBean()
            .inputArguments
            .toString()
            .contains("-agentlib:jdwp")

        if (isDebugging) {
            val context: LoggerContext = LoggerFactory.getILoggerFactory() as LoggerContext
            context.getLogger("ROOT").setLevel(Level.DEBUG)
        }

        JvmWarmup.warmup()
    }

    IntUiTheme(isDark = true) {
        Window(
            onCloseRequest = ::exitApplication,
            title = "CometProxy",
        ) {
            val coroutineScope = rememberCoroutineScope()
            val viewModel = remember { AppViewModel(coroutineScope) }

            Home(viewModel)
        }
    }
}