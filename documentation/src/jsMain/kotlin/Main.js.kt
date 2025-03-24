package com.juul.krayon.documentation

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.generated.resources.app_title
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import org.jetbrains.compose.resources.getString
import org.jetbrains.skiko.wasm.onWasmReady

@OptIn(ExperimentalComposeUiApi::class, DelicateCoroutinesApi::class)
fun main() {
    GlobalScope
        .promise { getString(Res.string.app_title) }
        .then { title ->
            onWasmReady {
                CanvasBasedWindow(title) { App() }
            }
        }
}
