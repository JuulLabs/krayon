package com.juul.krayon.documentation.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import com.juul.krayon.documentation.LocalCodeFontFamily
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.highlight
import dev.snipme.highlights.model.SyntaxTheme
import dev.snipme.highlights.model.SyntaxThemes
import org.jetbrains.compose.resources.ExperimentalResourceApi

@Composable
fun CodeView(path: String) {
    val darkMode = !MaterialTheme.colors.isLight
    val theme = remember(darkMode) { SyntaxThemes.monokai(darkMode) }

    var text by remember(path) { mutableStateOf<AnnotatedString?>(null) }
    LaunchedEffect(Unit) {
        text = load("files/samples/$path", theme)
    }

    text?.let {
        Text(it, fontFamily = LocalCodeFontFamily.current)
    }
}

@OptIn(ExperimentalResourceApi::class)
private suspend fun load(
    path: String,
    theme: SyntaxTheme = SyntaxThemes.default(),
): AnnotatedString = highlight(Res.readBytes(path).decodeToString(), theme)
