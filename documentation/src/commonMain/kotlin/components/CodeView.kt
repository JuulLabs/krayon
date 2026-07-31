package com.juul.krayon.documentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.generated.resources.Res
import com.juul.krayon.documentation.theme.AppTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi

/**
 * Displays the (syntax highlighted) source code of a chart sample.
 *
 * The Gradle build copies the sources under `src/commonMain/kotlin/samples` into the Compose
 * resources (see `syncDocumentationResources` in `build.gradle.kts`), guaranteeing that the code
 * shown is the exact code that was compiled into this application.
 *
 * @param path of the sample source file, relative to `src/commonMain/kotlin/samples`.
 */
@OptIn(ExperimentalResourceApi::class)
@Composable
fun SampleCodeView(path: String, modifier: Modifier = Modifier) {
    val syntaxTheme = AppTheme.highlight
    var code by remember(path) { mutableStateOf<AnnotatedString?>(null) }
    LaunchedEffect(path, syntaxTheme) {
        val text = Res.readBytes("files/samples/$path").decodeToString()
        code = withContext(Dispatchers.Default) { text.highlight(syntaxTheme) }
    }

    code?.let { CodeBlock(it, modifier) } ?: Loading(modifier)
}

/** Renders pre-highlighted code in a scrollable, lightly tinted block. */
@Composable
fun CodeBlock(code: AnnotatedString, modifier: Modifier = Modifier) {
    Text(
        text = code,
        style = AppTheme.typography.code,
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colors.onSurface.copy(alpha = 0.05f))
            .horizontalScroll(rememberScrollState())
            .padding(16.dp),
    )
}

/** Renders raw (not yet highlighted) Kotlin [code]. */
@Composable
fun CodeBlock(code: String, modifier: Modifier = Modifier) {
    val syntaxTheme = AppTheme.highlight
    val highlighted = remember(code, syntaxTheme) { code.trimIndent().highlight(syntaxTheme) }
    CodeBlock(highlighted, modifier)
}
