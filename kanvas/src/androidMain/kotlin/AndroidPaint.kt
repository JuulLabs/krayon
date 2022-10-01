package com.juul.krayon.kanvas

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.DashPathEffect
import android.graphics.LinearGradient
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.Typeface
import androidx.annotation.FontRes
import androidx.core.content.res.ResourcesCompat
import android.graphics.Paint as AndroidPaint

/** TODO: Double-check that this is the behavior used by other platforms. */
private val TILE_MODE = Shader.TileMode.CLAMP

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
    is Paint.Gradient -> androidPaint(this)
    is Paint.Text -> androidPaint(context, this)
    else -> throw IllegalArgumentException("Cannot convert `FillAndStroke` or `GradientAndStroke` paints to a Android native `Paint`.")
}

/** Converts a Krayon [Paint] into an [AndroidPaint]. */
public fun Paint.Fill.toAndroid(): AndroidPaint = androidPaint(this)

/** Converts a Krayon [Paint] into an [AndroidPaint]. */
public fun Paint.Stroke.toAndroid(): AndroidPaint = androidPaint(this)

/** Converts a Krayon [Paint] into an [AndroidPaint]. */
public fun Paint.Gradient.toAndroid(): AndroidPaint = androidPaint(this)

/** Converts a Krayon [Paint] into an [AndroidPaint]. */
public fun Paint.Text.toAndroid(context: Context): AndroidPaint = androidPaint(context, this)

private fun androidPaint(source: Paint.Fill) = AndroidPaint().apply {
    style = AndroidPaint.Style.FILL
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
    if (source.dash is Paint.Stroke.Dash.Pattern) {
        pathEffect = DashPathEffect(source.dash.intervals.toFloatArray(), 0f)
    }
}

private fun androidPaint(source: Paint.Gradient) = AndroidPaint().apply {
    style = AndroidPaint.Style.FILL
    isAntiAlias = true
    val colorStops = IntArray(source.stops.size)
    val positionStops = FloatArray(source.stops.size)
    source.stops.forEachIndexed { index, (offset, color) ->
        colorStops[index] = color.argb
        positionStops[index] = offset
    }
    shader = when (source) {
        is Paint.Gradient.Linear ->
            LinearGradient(source.startX, source.startY, source.endX, source.endY, colorStops, positionStops, TILE_MODE)
        is Paint.Gradient.Radial ->
            RadialGradient(source.centerX, source.centerY, source.radius, colorStops, positionStops, TILE_MODE)
        is Paint.Gradient.Sweep ->
            SweepGradient(source.centerX, source.centerY, colorStops, positionStops)
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
                        @SuppressLint("DiscouragedApi") // Result of this expensive call is cached.
                        // Local variable so suppress annotation can be applied to specific line.
                        val identifier = context.resources.getIdentifier(name, "font", context.packageName)
                        identifier
                    }
                }
            try {
                ResourcesCompat.getFont(context, resource)
            } catch (e: Resources.NotFoundException) {
                null
            }
        }.firstOrNull() ?: Typeface.DEFAULT
