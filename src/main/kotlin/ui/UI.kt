package dev.apollointhehouse.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberWindowState
import dev.apollointhehouse.network.proxy.ProxyManager
import dev.apollointhehouse.network.proxy.config.ProxyConfig
import dev.apollointhehouse.ui.model.AppViewModel
import dev.apollointhehouse.ui.repo.PlayerHeadRepository
import dev.apollointhehouse.ui.state.toProxyUIState
import dev.nucleusframework.application.nucleusApplication
import dev.nucleusframework.window.jewel.JewelDecoratedWindow
import dev.nucleusframework.window.jewel.JewelTitleBar
import org.jetbrains.jewel.foundation.DisabledAppearanceValues
import org.jetbrains.jewel.foundation.theme.JewelTheme
import org.jetbrains.jewel.intui.standalone.theme.IntUiTheme
import org.jetbrains.jewel.intui.standalone.theme.dark
import org.jetbrains.jewel.intui.standalone.theme.darkThemeDefinition
import org.jetbrains.jewel.intui.standalone.theme.default
import org.jetbrains.jewel.intui.window.decoratedWindow
import org.jetbrains.jewel.intui.window.styling.dark
import org.jetbrains.jewel.ui.ComponentStyling
import org.jetbrains.jewel.ui.typography
import org.jetbrains.jewel.window.styling.TitleBarStyle


fun UI(proxyConfig: ProxyConfig) = nucleusApplication {
    val state = rememberWindowState(
        size = DpSize(1000.dp, 700.dp),
        position = WindowPosition(Alignment.Center)
    )

    val themeDefinition = remember { JewelTheme.darkThemeDefinition(disabledAppearanceValues = DisabledAppearanceValues.dark()) }

    IntUiTheme(
        theme = themeDefinition,
        styling = ComponentStyling.default().decoratedWindow(titleBarStyle = TitleBarStyle.dark()),
    ) {
        JewelDecoratedWindow(
            onCloseRequest = ::exitApplication,
            title = "CometProxy",
            state = state,
            minimumSize = DpSize(640.dp, 480.dp),
        ) {
            val coroutineScope = rememberCoroutineScope()
            val viewModel = remember {
                AppViewModel(
                    initialState = proxyConfig.toProxyUIState(),
                    playerHeadRepo = PlayerHeadRepository(coroutineScope),
                    proxyManager = ProxyManager(),
                )
            }

            JewelTitleBar {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CometProxy",
                        style = JewelTheme.typography.h2TextStyle,
                        color = Color.White,
                    )
                }
            }

            Home(viewModel)
        }
    }
}