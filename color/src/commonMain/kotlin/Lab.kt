package com.juul.krayon.color

import kotlin.math.pow

// D50 reference white tristimulus values, matching d3-color.
private const val D50_WHITE_X = 0.96422f
private const val D50_WHITE_Y = 1f
private const val D50_WHITE_Z = 0.82521f

// One step of [Lab.brighter]/[Lab.darker], in units of lightness.
private const val LIGHTNESS_STEP = 18f

// CIE L* transfer-function constants, where delta = 6/29.
private const val CIE_DELTA = 6f / 29f
private const val CIE_OFFSET = 4f / 29f
private const val CIE_LINEAR_THRESHOLD = CIE_DELTA
private const val CIE_LINEAR_SCALE = 3f * CIE_DELTA * CIE_DELTA
private const val CIE_CUBIC_THRESHOLD = CIE_DELTA * CIE_DELTA * CIE_DELTA

/**
 * A color in the CIELAB color space.
 *
 * [lightness] is the L\* axis, [a] is the green–red axis, [b] is the blue–yellow axis, and
 * [opacity] is in `[0, 1]`.
 */
public data class Lab(
    public val lightness: Float,
    public val a: Float,
    public val b: Float,
    public val opacity: Float = 1f,
) {
    /** Returns a brighter copy of this color by increasing lightness. */
    public fun brighter(k: Float = 1f): Lab = Lab(lightness + LIGHTNESS_STEP * k, a, b, opacity)

    /** Returns a darker copy of this color by decreasing lightness. */
    public fun darker(k: Float = 1f): Lab = Lab(lightness - LIGHTNESS_STEP * k, a, b, opacity)
}

/** Converts this ARGB color to the CIELAB color space. */
public fun Color.toLab(): Lab {
    val linearRed = srgbToLinear(red.toFloat())
    val linearGreen = srgbToLinear(green.toFloat())
    val linearBlue = srgbToLinear(blue.toFloat())
    val x = xyzToLab((0.4360747f * linearRed + 0.3850649f * linearGreen + 0.1430804f * linearBlue) / D50_WHITE_X)
    val y: Float
    val z: Float
    if (red == green && green == blue) {
        y = x
        z = x
    } else {
        y = xyzToLab((0.2225045f * linearRed + 0.7168786f * linearGreen + 0.0606169f * linearBlue) / D50_WHITE_Y)
        z = xyzToLab((0.0139322f * linearRed + 0.0971045f * linearGreen + 0.7141733f * linearBlue) / D50_WHITE_Z)
    }
    return Lab(116f * y - 16f, 500f * (x - y), 200f * (y - z), opacityFraction())
}

/** Converts this CIELAB color to an ARGB [Color]. */
public fun Lab.toColor(): Color {
    var y = (lightness + 16f) / 116f
    var x = if (a.isNaN()) y else y + a / 500f
    var z = if (b.isNaN()) y else y - b / 200f
    x = D50_WHITE_X * labToXyz(x)
    y = D50_WHITE_Y * labToXyz(y)
    z = D50_WHITE_Z * labToXyz(z)
    return Color(
        alpha = opacityToAlpha(opacity),
        red = channelToInt(linearToSrgb(3.1338561f * x - 1.6168667f * y - 0.4906146f * z)),
        green = channelToInt(linearToSrgb(-0.9787684f * x + 1.9161415f * y + 0.0334540f * z)),
        blue = channelToInt(linearToSrgb(0.0719453f * x - 0.2289914f * y + 1.4052427f * z)),
    )
}

private fun xyzToLab(value: Float): Float =
    if (value > CIE_CUBIC_THRESHOLD) value.pow(1f / 3f) else value / CIE_LINEAR_SCALE + CIE_OFFSET

private fun labToXyz(value: Float): Float =
    if (value > CIE_LINEAR_THRESHOLD) value * value * value else CIE_LINEAR_SCALE * (value - CIE_OFFSET)
