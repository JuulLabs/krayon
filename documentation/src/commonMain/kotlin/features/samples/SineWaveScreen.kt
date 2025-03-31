package com.juul.krayon.documentation.features.samples

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.juul.krayon.documentation.features.samples.SineWaveViewModel.Code
import com.juul.krayon.documentation.highlight
import com.juul.krayon.documentation.samples.MovingSineWaveView
import com.juul.krayon.documentation.theme.AppTheme
import dev.snipme.highlights.model.SyntaxTheme

@Composable
fun SineWaveScreen(
    viewModel: SineWaveViewModel = viewModel { SineWaveViewModel() },
) {
    val code = viewModel.code.collectAsState().value
    if (code == null) {
        // Loading
    } else {
        Column {
            MovingSineWaveView(Modifier.fillMaxWidth().height(300.dp))

            val syntaxTheme = AppTheme.highlight
            val (data, updater, glue) = remember(syntaxTheme) { code.highlight(syntaxTheme) }

            Text("Data")
            Text(data, style = AppTheme.typography.code)
            Text("Updater")
            Text(updater, style = AppTheme.typography.code)
            Text("Glue")
            Text(glue, style = AppTheme.typography.code)
        }
    }
}

private data class HighlightedCode(
    val data: AnnotatedString,
    val updater: AnnotatedString,
    val glue: AnnotatedString,
)

private fun Code.highlight(syntaxTheme: SyntaxTheme) =
    HighlightedCode(
        data.highlight(syntaxTheme),
        updater.highlight(syntaxTheme),
        glue.highlight(syntaxTheme),
    )
