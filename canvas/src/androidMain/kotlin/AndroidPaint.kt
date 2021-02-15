package com.juul.krayon.canvas

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import androidx.core.content.res.ResourcesCompat
import android.graphics.Paint as AndroidPaint

/**
 * Cache for font name to identifier, since calling [Resources.getIdentifier] is expensive.
 *
 * TODO: Provide a way to pre-load into this map to avoid ever calling [Resources.getIdentifier]. This
 *       should also handle the use case where the font's logical name doesn't match the resource name.
 */
private val fontResources = mutableMapOf<String, Int>()

/** Converts a Krayon [Paint] into an [AndroidPaint] used by [AndroidCanvas]. */
public fun Paint.toAndroid(context: Context): AndroidPaint = when (this) {
    is Paint.Fill -> androidPaint(this)
    is Paint.Stroke -> androidPaint(this)
    is Paint.Text -> androidPaint(context, this)
}

private fun androidPaint(source: Paint.Fill) = AndroidPaint().apply {
    style = AndroidPaint.Style.STROKE
    isAntiAlias = true
    color = source.color.argb
}

private fun androidPaint(source: Paint.Stroke) = AndroidPaint().apply {
    style = AndroidPaint.Style.STROKE
    isAntiAlias = true
    color = source.color.argb
    strokeWidth = source.width
    strokeCap = when (source.cap) {
        Paint.Stroke.Cap.Butt -> AndroidPaint.Cap.BUTT
        Paint.Stroke.Cap.Round -> AndroidPaint.Cap.ROUND
        Paint.Stroke.Cap.Square -> AndroidPaint.Cap.SQUARE
    }
    strokeJoin = when (source.join) {
        is Paint.Stroke.Join.Miter -> AndroidPaint.Join.MITER
        Paint.Stroke.Join.Round -> AndroidPaint.Join.ROUND
        Paint.Stroke.Join.Bevel -> AndroidPaint.Join.BEVEL
    }
    if (source.join is Paint.Stroke.Join.Miter) {
        strokeMiter = source.join.limit
    }
}

private fun androidPaint(context: Context, source: Paint.Text) = AndroidPaint().apply {
    style = AndroidPaint.Style.FILL
    isAntiAlias = true
    color = source.color.argb
    // Scale text by the difference between dp and sp. By applying this globally, it means that
    // Krayon respect's a user's font scaling even in non-dp environments, like PDF rendering.
    val scale = with(context.resources.displayMetrics) { scaledDensity / density }
    textSize = source.size * scale
    typeface = getTypeface(context, source.font)
    textAlign = when (source.alignment) {
        Paint.Text.Alignment.Left -> AndroidPaint.Align.LEFT
        Paint.Text.Alignment.Center -> AndroidPaint.Align.CENTER
        Paint.Text.Alignment.Right -> AndroidPaint.Align.RIGHT
    }
}

private fun getTypeface(context: Context, font: Font): Typeface =
    when (font.name) {
        "sans-serif" -> Typeface.SANS_SERIF
        "serif" -> Typeface.SERIF
        else -> {
            val fontResource = fontResources.getOrPut(font.name) {
                context.resources.getIdentifier(font.name, "font", context.packageName)
            }
            try {
                ResourcesCompat.getFont(context, fontResource)
            } catch (e: Resources.NotFoundException) {
                null
            }
        }
    } ?: Typeface.DEFAULT
