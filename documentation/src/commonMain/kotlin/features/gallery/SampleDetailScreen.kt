package com.juul.krayon.documentation.features.gallery

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.juul.krayon.documentation.components.DemoCard
import com.juul.krayon.documentation.components.MarkdownBlock
import com.juul.krayon.documentation.components.SampleCodeView

@Composable
fun SampleDetailScreen(sample: Sample) {
    Column {
        Text(sample.title, style = MaterialTheme.typography.h4)
        MarkdownBlock(sample.description, Modifier.padding(vertical = 8.dp))
        Row {
            Text(
                text = "Modules: ${sample.modules.joinToString(", ")}",
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
            )
            if (sample.d3Counterpart != null && sample.d3Url != null) {
                Spacer(Modifier.width(16.dp))
                MarkdownBlock("D3 equivalent: [${sample.d3Counterpart}](${sample.d3Url})")
            }
        }
        DemoCard(height = 420.dp) {
            sample.content(Modifier.fillMaxSize(), true)
        }
        Text(
            text = "Source",
            style = MaterialTheme.typography.h6,
            modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
        )
        MarkdownBlock(
            """
            The code below is the exact code rendering the chart above — it is compiled into this
            page. The chart function is plain common Kotlin: the same code runs on Android, iOS,
            desktop, and the web.
            """,
        )
        SampleCodeView(sample.codePath, Modifier.padding(top = 8.dp))
    }
}
