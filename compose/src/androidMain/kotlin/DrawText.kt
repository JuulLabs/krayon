package com.juul.krayon.compose

import androidx.compose.ui.graphics.nativeCanvas
import com.juul.krayon.kanvas.Paint
import android.graphics.Paint as AndroidPaint

internal actual fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
    val canvas = kanvas.scope.drawContext.canvas.nativeCanvas
    canvas.drawText(text.toString(), x, y, androidPaint(paint))
}

private fun androidPaint(source: Paint.Text) = AndroidPaint().apply {
    style = AndroidPaint.Style.FILL
    isAntiAlias = true
    color = source.color.argb
    textSize = source.size
    typeface = source.font.toNativeTypeface()
    textAlign = when (source.alignment) {
        Paint.Text.Alignment.Left -> AndroidPaint.Align.LEFT
        Paint.Text.Alignment.Center -> AndroidPaint.Align.CENTER
        Paint.Text.Alignment.Right -> AndroidPaint.Align.RIGHT
    }
}
