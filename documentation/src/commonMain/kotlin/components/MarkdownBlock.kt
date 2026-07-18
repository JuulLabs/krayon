package com.juul.krayon.documentation.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.juul.krayon.documentation.theme.AppTheme
import com.mikepenz.markdown.compose.components.markdownComponents
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeBlock
import com.mikepenz.markdown.compose.elements.MarkdownHighlightedCodeFence
import com.mikepenz.markdown.m2.Markdown
import com.mikepenz.markdown.m2.markdownTypography
import dev.snipme.highlights.Highlights

/**
 * Renders documentation prose written as Markdown, with Kotlin syntax highlighting applied to
 * fenced code blocks.
 */
@Composable
fun MarkdownBlock(content: String, modifier: Modifier = Modifier) {
    val codeStyle = AppTheme.typography.code
    val syntaxTheme = AppTheme.highlight
    val highlightsBuilder = remember(syntaxTheme) { Highlights.Builder().theme(syntaxTheme) }
    Markdown(
        content = content.trimIndent(),
        components = markdownComponents(
            codeBlock = { model ->
                MarkdownHighlightedCodeBlock(
                    content = model.content,
                    node = model.node,
                    style = codeStyle,
                    highlightsBuilder = highlightsBuilder,
                )
            },
            codeFence = { model ->
                MarkdownHighlightedCodeFence(
                    content = model.content,
                    node = model.node,
                    style = codeStyle,
                    highlightsBuilder = highlightsBuilder,
                )
            },
        ),
        typography = markdownTypography(
            code = codeStyle,
            inlineCode = codeStyle,
        ),
        modifier = modifier,
    )
}
