package com.juul.krayon.color

import kotlin.math.pow

private const val XN = 0.96422f
private const val YN = 1f
private const val ZN = 0.82521f
private const val T0 = 4f / 29f
private const val T1 = 6f / 29f
private val T2 = 3f * T1 * T1
private val T3 = T1 * T1 * T1
private const val K = 18f

/**
 * A color in the CIELAB color space.
 *
 * [l] is lightness, [a] and [b] are the chromatic axes, and [opacity] is in `[0, 1]`.
 */
public data class Lab(
    public val l: Float,
    public val a: Float,
    public val b: Float,
    public val opacity: Float = 1f,
) {
    /** Returns a brighter copy of this color by increasing lightness. */
    public fun brighter(k: Float = 1f): Lab = Lab(l + K * k, a, b, opacity)

    /** Returns a darker copy of this color by decreasing lightness. */
    public fun darker(k: Float = 1f): Lab = Lab(l - K * k, a, b, opacity)
}

/** Converts this ARGB color to the CIELAB color space. */
public fun Color.toLab(): Lab {
    val r = rgb2lrgb(red.toFloat())
    val g = rgb2lrgb(green.toFloat())
    val bl = rgb2lrgb(blue.toFloat())
    val x = xyz2lab((0.4360747f * r + 0.3850649f * g + 0.1430804f * bl) / XN)
    val y: Float
    val z: Float
    if (red == green && green == blue) {
        y = x
        z = x
    } else {
        y = xyz2lab((0.2225045f * r + 0.7168786f * g + 0.0606169f * bl) / YN)
        z = xyz2lab((0.0139322f * r + 0.0971045f * g + 0.7141733f * bl) / ZN)
    }
    return Lab(116f * y - 16f, 500f * (x - y), 200f * (y - z), opacityFraction())
}

/** Converts this CIELAB color to an ARGB [Color]. */
public fun Lab.toColor(): Color {
    var y = (l + 16f) / 116f
    var x = if (a.isNaN()) y else y + a / 500f
    var z = if (b.isNaN()) y else y - b / 200f
    x = XN * lab2xyz(x)
    y = YN * lab2xyz(y)
    z = ZN * lab2xyz(z)
    return Color(
        alpha = opacityToAlpha(opacity),
        red = channelToInt(lrgb2rgb(3.1338561f * x - 1.6168667f * y - 0.4906146f * z)),
        green = channelToInt(lrgb2rgb(-0.9787684f * x + 1.9161415f * y + 0.0334540f * z)),
        blue = channelToInt(lrgb2rgb(0.0719453f * x - 0.2289914f * y + 1.4052427f * z)),
    )
}

private fun xyz2lab(t: Float): Float = if (t > T3) t.pow(1f / 3f) else t / T2 + T0

private fun lab2xyz(t: Float): Float = if (t > T1) t * t * t else T2 * (t - T0)
