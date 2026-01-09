package com.juul.krayon.compose

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juul.krayon.kanvas.Kanvas

@Composable
public fun Kanvas(
    modifier: Modifier,
    onDraw: ComposeKanvas.() -> Unit,
) {
    Canvas(modifier) {
        onDraw(ComposeKanvas(scope = this))
    }
}
