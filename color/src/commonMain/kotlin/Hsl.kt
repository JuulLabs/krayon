package com.juul.krayon.color

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * A color in the HSL (hue, saturation, lightness) color space.
 *
 * [h] is in degrees `[0, 360)`, [s] and [l] are in `[0, 1]`, and [opacity] is in `[0, 1]`. An
 * achromatic color has a hue (and possibly saturation) of [Float.NaN], mirroring d3-color.
 */
public data class Hsl(
    public val h: Float,
    public val s: Float,
    public val l: Float,
    public val opacity: Float = 1f,
) {
    /** Returns a brighter copy of this color by scaling lightness. */
    public fun brighter(k: Float = 1f): Hsl = Hsl(h, s, l * (1f / DARKER).pow(k), opacity)

    /** Returns a darker copy of this color by scaling lightness. */
    public fun darker(k: Float = 1f): Hsl = Hsl(h, s, l * DARKER.pow(k), opacity)
}

/** Converts this ARGB color to the HSL color space. */
public fun Color.toHsl(): Hsl {
    val r = redFraction()
    val g = greenFraction()
    val b = blueFraction()
    val min = min(r, min(g, b))
    val max = max(r, max(g, b))
    var h = Float.NaN
    var s = max - min
    val l = (max + min) / 2f
    if (s != 0f) {
        h = when (max) {
            r -> (g - b) / s + (if (g < b) 6f else 0f)
            g -> (b - r) / s + 2f
            else -> (r - g) / s + 4f
        }
        s /= if (l < 0.5f) max + min else 2f - max - min
        h *= 60f
    } else {
        s = if (l > 0f && l < 1f) 0f else Float.NaN
    }
    return Hsl(h, s, l, opacityFraction())
}

/** Converts this HSL color to an ARGB [Color]. */
public fun Hsl.toColor(): Color {
    val hue = if (h.isNaN()) 0f else (h % 360f).let { if (it < 0f) it + 360f else it }
    val sat = if (s.isNaN()) 0f else s
    val m2 = l + (if (l < 0.5f) l else 1f - l) * sat
    val m1 = 2f * l - m2
    return Color(
        alpha = opacityToAlpha(opacity),
        red = channelToInt(hsl2rgb(if (hue >= 240f) hue - 240f else hue + 120f, m1, m2)),
        green = channelToInt(hsl2rgb(hue, m1, m2)),
        blue = channelToInt(hsl2rgb(if (hue < 120f) hue + 240f else hue - 120f, m1, m2)),
    )
}

private fun hsl2rgb(h: Float, m1: Float, m2: Float): Float {
    val v = when {
        h < 60f -> m1 + (m2 - m1) * h / 60f
        h < 180f -> m2
        h < 240f -> m1 + (m2 - m1) * (240f - h) / 60f
        else -> m1
    }
    return v * 255f
}
