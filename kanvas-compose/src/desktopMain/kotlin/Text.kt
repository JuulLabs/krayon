package com.juul.krayon.kanvas.compose

import androidx.compose.ui.graphics.nativeCanvas
import com.juul.krayon.kanvas.Paint
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.TextLine
import org.jetbrains.skia.Typeface
import org.jetbrains.skia.Font as SkiaFont
import org.jetbrains.skia.Paint as SkiaPaint

internal actual fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
    val canvas = kanvas.scope.drawContext.canvas.nativeCanvas
    val textLine = text.toTextLine(paint)
    canvas.drawTextLine(
        line = textLine,
        x = when (paint.alignment) {
            Paint.Text.Alignment.Left -> x
            Paint.Text.Alignment.Center -> x - (0.5f * textLine.width)
            Paint.Text.Alignment.Right -> x - textLine.width
        },
        y = y,
        paint = paint.toSkiaPaint()
    )
}

private fun CharSequence.toTextLine(paint: Paint.Text): TextLine =
    TextLine.make(this.toString(), paint.toSkiaFont())

private fun Paint.Text.toSkiaFont(): SkiaFont {
    // TODO: Verify that this font behavior works as desired when using bold-fonts explicitly by name.
    val typeface = Typeface.makeFromName(font.names.first(), FontStyle.NORMAL)
    return SkiaFont(typeface, size)
}

private fun Paint.Text.toSkiaPaint(): SkiaPaint {
    val paint = SkiaPaint()
    paint.color = color.argb
    return paint
}
