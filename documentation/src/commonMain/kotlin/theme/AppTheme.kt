package com.juul.krayon.documentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.generated.resources.roboto_mono_regular
import dev.snipme.highlights.model.SyntaxTheme
import dev.snipme.highlights.model.SyntaxThemes
import org.jetbrains.compose.resources.Font

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (darkTheme) darkColors() else lightColors(),
    ) {
        val robotoMonoFont = Font(Res.font.roboto_mono_regular)
        CompositionLocalProvider(
            LocalTypography provides Typography(TextStyle(fontFamily = FontFamily(robotoMonoFont))),
            LocalSyntaxTheme provides SyntaxThemes.monokai(darkTheme),
        ) {
            content()
        }
    }
}

object AppTheme {

    val colors: Colors
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colors

    val typography: Typography
        @Composable
        @ReadOnlyComposable
        get() = LocalTypography.current

    val highlight: SyntaxTheme
        @Composable
        @ReadOnlyComposable
        get() = LocalSyntaxTheme.current
}
