package ui

import SharedModule
import WebModule
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.ComposeViewport
import io.ktor.client.*
import io.ktor.client.plugins.cookies.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.koin.compose.KoinContext
import org.koin.core.context.GlobalContext.startKoin
import services.ApiUrl
import ui.theme.DnDHelperTheme

private fun addCreds() {
    js(
        """
    window.originalFetch = window.fetch;
    window.fetch = function (resource, init) {
        init = Object.assign({}, init);
        init.credentials = init.credentials !== undefined ? init.credentials : 'include';
        return window.originalFetch(resource, init);
    };
"""
    )
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalFoundationApi::class, ExperimentalMaterialApi::class)
fun main() {
    ComposeViewport(document.body!!) {
        addCreds()
        val scope = rememberCoroutineScope()
        val client = HttpClient {
            install(HttpCookies)
        }

        // TODO: Update sign in checking.
        val text = remember { mutableStateOf("") }
        scope.launch {
            val response = client.get {
                this.url {
                    this.host = ApiUrl
                    this.port = 8080
                }
            }
            text.value = response.bodyAsText()
        }


        DnDHelperTheme {
            when (text.value) {
                "0" -> SignInView()
                "1" -> {
                    initKoin()
                    KoinContext {
                        RootView()
                    }
                }
            }
        }
    }
}

fun initKoin() {
    startKoin {
        modules(SharedModule().module, WebModule().module)
    }
}