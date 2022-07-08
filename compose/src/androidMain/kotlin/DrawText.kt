package com.juul.krayon.compose

import androidx.compose.ui.graphics.nativeCanvas
import com.juul.krayon.kanvas.Paint

internal actual fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
    val canvas = kanvas.scope.drawContext.canvas.nativeCanvas
    canvas.drawText(text.toString(), x, y, kanvas.resourceCache.cache[paint])
}
