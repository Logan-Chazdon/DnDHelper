package ui

import SharedModule
import WebModule
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import kotlinx.browser.document
import org.koin.compose.KoinContext
import org.koin.core.context.GlobalContext.startKoin
import ui.theme.DnDHelperTheme


@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
fun main() {
    initKoin()
    ComposeViewport(document.body!!) {
        KoinContext {
            DnDHelperTheme {
                RootView()
            }
        }
    }
}


fun initKoin() {
    startKoin {
        modules(SharedModule().module, WebModule().module)
    }
}