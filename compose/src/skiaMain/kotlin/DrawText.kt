package com.juul.krayon.compose

import androidx.compose.ui.graphics.nativeCanvas
import com.juul.krayon.kanvas.Paint
import org.jetbrains.skia.FontHinting
import org.jetbrains.skia.impl.use
import org.jetbrains.skia.Font as SkiaFont
import org.jetbrains.skia.Paint as SkiaPaint

internal actual fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
    if (text.isEmpty()) return
    paint.toSkiaFont().use { font ->
        val canvas = kanvas.scope.drawContext.canvas.nativeCanvas
        val string = text.toString()
        val skiaPaint = paint.toSkiaPaint()
        canvas.drawString(
            string,
            x = when (paint.alignment) {
                Paint.Text.Alignment.Left -> x
                Paint.Text.Alignment.Center -> x - (0.5f * measureTextWidth(string, font, skiaPaint))
                Paint.Text.Alignment.Right -> x - measureTextWidth(string, font, skiaPaint)
            },
            y = y,
            font = font,
            paint = skiaPaint,
        )
        skiaPaint.close()
    }
}

private fun Paint.Text.toSkiaFont(): SkiaFont {
    val font = SkiaFont(font.toNativeTypeface(), size)
    font.isSubpixel = true
    return font
}

private fun Paint.Text.toSkiaPaint(): SkiaPaint {
    val paint = SkiaPaint()
    paint.color = color.argb
    return paint
}

private fun measureTextWidth(text: String, font: SkiaFont, paint: SkiaPaint): Float {
    // TODO: Figure out why `font.measureTextWidth(text, paint)` doesn't return the same thing every time.
    //       This appears to be a bug on web only, but causes really painful text jumpiness.
    val glyphs = font.getStringGlyphs(text)
    var totalWidth = 0f
    for (rect in font.getBounds(glyphs, paint)) {
        totalWidth += rect.width
    }
    return totalWidth
}
