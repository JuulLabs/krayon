package com.juul.krayon.documentation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.text.font.FontFamily
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.generated.resources.roboto_mono_regular
import org.jetbrains.compose.resources.Font

private val LightColors = lightColors()
private val DarkColors = darkColors()

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalCodeFontFamily provides FontFamily(Font(Res.font.roboto_mono_regular))) {
        MaterialTheme(
            colors = if (darkTheme) DarkColors else LightColors,
            content = content,
        )
    }
}
