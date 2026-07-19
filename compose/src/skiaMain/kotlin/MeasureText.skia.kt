package com.juul.krayon.compose

import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.TextMetrics
import org.jetbrains.skia.impl.use

internal actual fun measureText(text: CharSequence, paint: Paint.Text): TextMetrics =
    paint.toSkiaFont().use { font ->
        val metrics = font.metrics
        val width = if (text.isEmpty()) {
            0f
        } else {
            val skiaPaint = paint.toSkiaPaint()
            try {
                measureTextWidth(text.toString(), font, skiaPaint)
            } finally {
                skiaPaint.close()
            }
        }
        TextMetrics(
            width = width,
            ascent = -metrics.ascent,
            descent = metrics.descent,
        )
    }
