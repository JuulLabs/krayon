package com.juul.krayon.documentation

import androidx.compose.ui.window.singleWindowApplication
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.generated.resources.app_title
import kotlinx.coroutines.runBlocking
import org.jetbrains.compose.resources.getString

fun main() {
    val title = runBlocking { getString(Res.string.app_title) }
    singleWindowApplication(title = title) { App() }
}
