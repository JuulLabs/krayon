package com.juul.krayon.color

import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

/**
 * A color in the HSL (hue, saturation, lightness) color space.
 *
 * [hue] is in degrees `[0, 360)`, [saturation] and [lightness] are in `[0, 1]`, and [opacity] is in
 * `[0, 1]`. An achromatic color has a hue (and possibly saturation) of [Float.NaN], mirroring
 * d3-color.
 */
public data class Hsl(
    public val hue: Float,
    public val saturation: Float,
    public val lightness: Float,
    public val opacity: Float = 1f,
) {
    /** Returns a brighter copy of this color by scaling lightness. */
    public fun brighter(k: Float = 1f): Hsl = Hsl(hue, saturation, lightness * (1f / DARKER).pow(k), opacity)

    /** Returns a darker copy of this color by scaling lightness. */
    public fun darker(k: Float = 1f): Hsl = Hsl(hue, saturation, lightness * DARKER.pow(k), opacity)
}

/** Converts this ARGB color to the HSL color space. */
public fun Color.toHsl(): Hsl {
    val red = redFraction()
    val green = greenFraction()
    val blue = blueFraction()
    val minimum = min(red, min(green, blue))
    val maximum = max(red, max(green, blue))
    var hue = Float.NaN
    var saturation = maximum - minimum
    val lightness = (maximum + minimum) / 2f
    if (saturation != 0f) {
        hue = when (maximum) {
            red -> (green - blue) / saturation + (if (green < blue) 6f else 0f)
            green -> (blue - red) / saturation + 2f
            else -> (red - green) / saturation + 4f
        }
        saturation /= if (lightness < 0.5f) maximum + minimum else 2f - maximum - minimum
        hue *= 60f
    } else {
        saturation = if (lightness > 0f && lightness < 1f) 0f else Float.NaN
    }
    return Hsl(hue, saturation, lightness, opacityFraction())
}

/** Converts this HSL color to an ARGB [Color]. */
public fun Hsl.toColor(): Color {
    val normalizedHue = if (hue.isNaN()) 0f else (hue % 360f).let { if (it < 0f) it + 360f else it }
    val normalizedSaturation = if (saturation.isNaN()) 0f else saturation
    val upper = lightness + (if (lightness < 0.5f) lightness else 1f - lightness) * normalizedSaturation
    val lower = 2f * lightness - upper
    return Color(
        alpha = opacityToAlpha(opacity),
        red = channelToInt(hueToRgb(if (normalizedHue >= 240f) normalizedHue - 240f else normalizedHue + 120f, lower, upper)),
        green = channelToInt(hueToRgb(normalizedHue, lower, upper)),
        blue = channelToInt(hueToRgb(if (normalizedHue < 120f) normalizedHue + 240f else normalizedHue - 120f, lower, upper)),
    )
}

private fun hueToRgb(hue: Float, lower: Float, upper: Float): Float {
    val value = when {
        hue < 60f -> lower + (upper - lower) * hue / 60f
        hue < 180f -> upper
        hue < 240f -> lower + (upper - lower) * (240f - hue) / 60f
        else -> lower
    }
    return value * 255f
}
