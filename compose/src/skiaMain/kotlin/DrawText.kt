package com.juul.krayon.compose

import androidx.compose.ui.graphics.nativeCanvas
import com.juul.krayon.kanvas.Paint
import org.jetbrains.skia.impl.use
import org.jetbrains.skia.Font as SkiaFont
import org.jetbrains.skia.Paint as SkiaPaint

internal actual fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
    paint.toSkiaFont().use { font ->
        val canvas = kanvas.scope.drawContext.canvas.nativeCanvas
        val string = text.toString()
        canvas.drawString(
            string,
            x = when (paint.alignment) {
                Paint.Text.Alignment.Left -> x
                Paint.Text.Alignment.Center -> x - (0.5f * font.measureTextWidth(string))
                Paint.Text.Alignment.Right -> x - font.measureTextWidth(string)
            },
            y = y,
            font = font,
            paint = paint.toSkiaPaint(),
        )
    }
}

private fun Paint.Text.toSkiaFont(): SkiaFont =
    SkiaFont(font.toNativeTypeface(), size)

private fun Paint.Text.toSkiaPaint(): SkiaPaint {
    val paint = SkiaPaint()
    paint.color = color.argb
    return paint
}
