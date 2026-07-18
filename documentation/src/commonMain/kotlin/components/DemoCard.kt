package com.juul.krayon.documentation.components

import androidx.compose.foundation.border
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/** Frames a live chart demo, with an optional caption below it. */
@Composable
fun DemoCard(
    modifier: Modifier = Modifier,
    height: Dp = 280.dp,
    caption: String? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Column(
            Modifier
                .fillMaxWidth()
                .height(height)
                .clip(RoundedCornerShape(8.dp))
                .border(1.dp, MaterialTheme.colors.onSurface.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
                .padding(8.dp),
        ) {
            content()
        }
        if (caption != null) {
            Text(
                text = caption,
                style = MaterialTheme.typography.caption,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}
