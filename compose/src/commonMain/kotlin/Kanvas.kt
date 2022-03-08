package com.juul.krayon.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Path
import com.juul.krayon.kanvas.Kanvas

@Composable
public fun Kanvas(
    modifier: Modifier,
    onDraw: Kanvas<Path>.() -> Unit,
) {
    val resourceCache = rememberResourceCache()
    Canvas(modifier) {
        onDraw(ComposeKanvas(scope = this, resourceCache))
    }
}
