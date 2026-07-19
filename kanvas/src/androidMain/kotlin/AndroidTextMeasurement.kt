package com.juul.krayon.kanvas

import android.content.Context

/**
 * Standalone [MeasureText] implementation backed by Android's native text rendering. Useful for
 * measuring text during layout, when an [AndroidKanvas] is not available.
 *
 * Reuses [PaintCache] so that measurements account for the same font, size, and user font-scaling
 * that [AndroidKanvas] applies when drawing.
 */
public class AndroidTextMeasurement(
    context: Context,
    private val paintCache: PaintCache = PaintCache(context),
) : MeasureText {

    override fun measureText(text: CharSequence, paint: Paint.Text): TextMetrics {
        val nativePaint = paintCache[paint]
        val width = nativePaint.measureText(text, 0, text.length)
        val fontMetrics = nativePaint.fontMetrics
        return TextMetrics(
            width = width,
            ascent = -fontMetrics.ascent,
            descent = fontMetrics.descent,
        )
    }
}
