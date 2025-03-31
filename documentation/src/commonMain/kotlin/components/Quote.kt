package com.juul.krayon.documentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun Quote(
    width: Dp = 2.dp,
    color: Color = MaterialTheme.colors.onSurface,
    content: @Composable () -> Unit,
) {
    Row(Modifier.height(IntrinsicSize.Max)) {
        Box(Modifier.fillMaxHeight().width(width).background(color))
        Spacer(Modifier.size(5.dp))
        content()
    }
}
