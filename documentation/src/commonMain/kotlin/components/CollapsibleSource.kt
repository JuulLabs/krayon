package com.juul.krayon.documentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/** An expandable section for supplementary source code, collapsed by default. */
@Composable
fun CollapsibleSource(
    label: String,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    val chevronRotation by animateFloatAsState(if (expanded) 0f else -90f)
    Column(modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
        ) {
            Chevron(rotation = chevronRotation)
            Text(
                text = label,
                style = MaterialTheme.typography.body2,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colors.primary,
            )
        }
        AnimatedVisibility(expanded) {
            content()
        }
    }
}

/** A small downward-pointing triangle, in keeping with a drawing library's documentation. */
@Composable
private fun Chevron(rotation: Float) {
    val color = MaterialTheme.colors.primary
    Canvas(
        Modifier
            .padding(end = 6.dp)
            .size(10.dp)
            .rotate(rotation),
    ) {
        drawPath(
            path = Path().apply {
                moveTo(0f, size.height * 0.25f)
                lineTo(size.width, size.height * 0.25f)
                lineTo(size.width / 2f, size.height * 0.75f)
                close()
            },
            color = color,
        )
    }
}
