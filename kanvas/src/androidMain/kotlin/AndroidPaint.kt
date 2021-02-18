package com.juul.krayon.kanvas

import android.content.Context
import android.content.res.Resources
import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import android.graphics.Paint as AndroidPaint

/** Cache from [Font.name] to [FontRes] id. */
private val fontResources = mutableMapOf<String, Int>()

/**
 * Associate a [Font.name] to a [FontRes] id. As a best-practice, do this up front for any
 * fonts that might be used, as calls to [addFontAssociation] are cheap and the behavior
 * for missing associations is expensive ([Resources.getIdentifier]).
 */
public fun addFontAssociation(fontName: String, @FontRes fontRes: Int) {
    fontResources[fontName] = fontRes
}

/** Converts a Krayon [Paint] into an [AndroidPaint]. */
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
    font.names.asSequence()
        .mapNotNull { name ->
            val resource = fontResources[name]
                ?: when (name) {
                    serif -> return@mapNotNull Typeface.SERIF
                    sansSerif -> return@mapNotNull Typeface.SANS_SERIF
                    monospace -> return@mapNotNull Typeface.MONOSPACE
                    else -> fontResources.getOrPut(name) {
                        context.resources.getIdentifier(name, "font", context.packageName)
                    }
                }
            try {
                ResourcesCompat.getFont(context, resource)
            } catch (e: Resources.NotFoundException) {
                null
            }
        }.firstOrNull() ?: Typeface.DEFAULT
