package com.juul.krayon.documentation.features.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.theme.PaperTheme

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GalleryScreen(onSampleClick: (Sample) -> Unit) {
    Column {
        Text("Gallery", style = MaterialTheme.typography.h4)
        MarkdownBlock(
            """
            Every chart below is rendered live by Krayon, right here in your browser.
            Click any of them to see the full example with its source code.
            Many are ports of classic [D3 examples](https://observablehq.com/@d3/gallery).
            """,
            Modifier.padding(vertical = 8.dp),
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
        ) {
            samples.forEach { sample ->
                SampleCard(sample) { onSampleClick(sample) }
            }
        }
    }
}

@Composable
private fun SampleCard(sample: Sample, onClick: () -> Unit) {
    // Charts draw with light-background colors, so cards are always "paper" (see PaperTheme).
    PaperTheme {
        Column(
            Modifier
                .width(272.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colors.surface)
                .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                .clickable(onClick = onClick)
                .padding(12.dp),
        ) {
            Box(Modifier.fillMaxWidth().height(160.dp)) {
                sample.content(Modifier.fillMaxWidth().height(160.dp), false)
            }
            Text(
                text = sample.title,
                style = MaterialTheme.typography.subtitle1,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.onSurface,
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                text = sample.modules.joinToString(" · "),
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            )
        }
    }
}
