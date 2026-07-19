package com.juul.krayon.documentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.generated.resources.roboto_mono_regular
import dev.snipme.highlights.model.SyntaxTheme
import dev.snipme.highlights.model.SyntaxThemes
import org.jetbrains.compose.resources.Font

/** Roughly matches the steel blue used throughout the chart samples. */
private val KrayonBlue = Color(0xFF4682B4)
private val KrayonBlueDark = Color(0xFF35648D)
private val KrayonBlueLight = Color(0xFF89B4D4)

/** Whether the app is currently displayed in dark mode. */
val LocalDarkMode: ProvidableCompositionLocal<Boolean> = staticCompositionLocalOf { false }

/**
 * A always-light "paper" theme for the panels that charts are drawn on.
 *
 * The chart samples are teaching material with hardcoded colors (black axes, web colors), so
 * rather than complicating them with theme plumbing, charts — and the controls that sit next to
 * them — are always presented on light panels, like plots in a dark-mode notebook.
 */
@Composable
fun PaperTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = lightColors(
            primary = KrayonBlue,
            primaryVariant = KrayonBlueDark,
            secondary = KrayonBlue,
        ),
        content = content,
    )
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = if (darkTheme) {
            darkColors(
                primary = KrayonBlueLight,
                primaryVariant = KrayonBlue,
                secondary = KrayonBlueLight,
            )
        } else {
            lightColors(
                primary = KrayonBlue,
                primaryVariant = KrayonBlueDark,
                secondary = KrayonBlue,
            )
        },
    ) {
        val robotoMonoFont = Font(Res.font.roboto_mono_regular)
        CompositionLocalProvider(
            LocalDarkMode provides darkTheme,
            LocalTypography provides Typography(
                code = TextStyle(
                    fontFamily = FontFamily(robotoMonoFont),
                    fontSize = 13.sp,
                    lineHeight = 20.sp,
                ),
            ),
            LocalSyntaxTheme provides SyntaxThemes.pastel(darkMode = darkTheme),
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
