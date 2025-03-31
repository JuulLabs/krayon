package com.juul.krayon.documentation.theme

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.staticCompositionLocalOf
import dev.snipme.highlights.model.SyntaxTheme
import dev.snipme.highlights.model.SyntaxThemes

val LocalSyntaxTheme: ProvidableCompositionLocal<SyntaxTheme> =
    staticCompositionLocalOf { SyntaxThemes.default() }
