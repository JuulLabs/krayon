package com.juul.krayon.compose

import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.TextMetrics

internal actual fun measureText(text: CharSequence, paint: Paint.Text): TextMetrics {
    val nativePaint = androidPaint(paint)
    val width = nativePaint.measureText(text, 0, text.length)
    val fontMetrics = nativePaint.fontMetrics
    return TextMetrics(
        width = width,
        ascent = -fontMetrics.ascent,
        descent = fontMetrics.descent,
    )
}
