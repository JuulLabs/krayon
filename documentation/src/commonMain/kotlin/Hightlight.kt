package com.juul.krayon.documentation

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import dev.snipme.highlights.Highlights
import dev.snipme.highlights.model.BoldHighlight
import dev.snipme.highlights.model.ColorHighlight
import dev.snipme.highlights.model.SyntaxLanguage
import dev.snipme.highlights.model.SyntaxLanguage.KOTLIN
import dev.snipme.highlights.model.SyntaxTheme

fun String.highlight(
    theme: SyntaxTheme,
    language: SyntaxLanguage = KOTLIN,
): AnnotatedString {
    val highlights = Highlights.Builder()
        .code(this)
        .theme(theme)
        .language(language)
        .build()
        .getHighlights()

    return buildAnnotatedString {
        append(this@highlight)
        highlights.forEach {
            when (it) {
                is BoldHighlight -> addStyle(
                    SpanStyle(fontWeight = FontWeight.Bold),
                    start = it.location.start,
                    end = it.location.end,
                )

                is ColorHighlight -> addStyle(
                    SpanStyle(color = Color(it.rgb).copy(alpha = 1f)),
                    start = it.location.start,
                    end = it.location.end,
                )
            }
        }
    }
}
