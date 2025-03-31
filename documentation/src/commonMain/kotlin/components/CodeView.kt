package com.juul.krayon.documentation.components

import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.highlight
import com.juul.krayon.documentation.theme.AppTheme
import dev.snipme.highlights.model.SyntaxTheme
import dev.snipme.highlights.model.SyntaxThemes

@Composable
fun CodeView(path: String) {
    val highlight = AppTheme.highlight
    var text by remember(path) { mutableStateOf<AnnotatedString?>(null) }
    LaunchedEffect(Unit) {
        text = load("files/samples/$path", highlight)
    }

    text?.let {
        Text(it, style = AppTheme.typography.code)
    }
}

private suspend fun load(
    path: String,
    theme: SyntaxTheme = SyntaxThemes.default(),
): AnnotatedString = Res.readBytes(path).decodeToString().highlight(theme)
