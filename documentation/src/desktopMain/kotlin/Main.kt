package com.juul.krayon.documentation

import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState

fun main() {
    application {
        Window(
            onCloseRequest = ::exitApplication,
            title = "Krayon",
            state = rememberWindowState(width = 1280.dp, height = 900.dp),
        ) {
            App()
        }
    }
}
