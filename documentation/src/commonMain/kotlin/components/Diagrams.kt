package com.juul.krayon.documentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.juul.krayon.compose.ElementView
import com.juul.krayon.documentation.samples.classStructureDiagram
import com.juul.krayon.documentation.samples.dataToDomDiagram

/**
 * White panel framing for diagrams, readable in both light and dark themes, followed by the
 * source code that draws the diagram (dogfooding: the diagrams are drawn by Krayon itself).
 *
 * @param codePath source file of the diagram, relative to `src/commonMain/kotlin/samples`.
 */
@Composable
private fun DiagramPanel(
    caption: String,
    height: Dp,
    codePath: String,
    content: @Composable () -> Unit,
) {
    Column(Modifier.padding(vertical = 8.dp)) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(8.dp))
                .background(Color.White)
                .padding(8.dp),
        ) {
            content()
        }
        Text(
            text = caption,
            style = MaterialTheme.typography.caption,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            modifier = Modifier.padding(top = 4.dp),
        )
        CollapsibleSource(label = "Show the code that draws this diagram ($codePath)", modifier = Modifier.padding(top = 4.dp)) {
            SampleCodeView(codePath)
        }
    }
}

@Composable
fun DataToDomDiagram(modifier: Modifier = Modifier) {
    DiagramPanel(
        caption = "Rendered live by Krayon — not an image.",
        height = 120.dp,
        codePath = "DataToDomDiagram.kt",
    ) {
        ElementView({ }, ::dataToDomDiagram, modifier.fillMaxWidth().height(104.dp))
    }
}

@Composable
fun ClassStructureDiagram(modifier: Modifier = Modifier) {
    DiagramPanel(
        caption = "Rendered live by Krayon — not an image.",
        height = 392.dp,
        codePath = "ClassStructureDiagram.kt",
    ) {
        ElementView({ }, ::classStructureDiagram, modifier.fillMaxWidth().height(376.dp))
    }
}

/** The shared box-and-arrow renderer's source, shown on the rendering concepts page. */
@Composable
fun DiagramRendererSource(modifier: Modifier = Modifier) {
    CollapsibleSource(label = "Show the shared renderer (DiagramRenderer.kt)", modifier = modifier) {
        SampleCodeView("DiagramRenderer.kt")
    }
}
