package com.juul.krayon.color

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

private const val A = -0.14861f
private const val B = 1.78277f
private const val C = -0.29227f
private const val D = -0.90649f
private const val E = 1.97294f
private val ED = E * D
private val EB = E * B
private val BC_DA = B * C - D * A

/**
 * A color in Dave Green's Cubehelix color space.
 *
 * [h] is hue in degrees, [s] is saturation, [l] is lightness in `[0, 1]`, and [opacity] is in
 * `[0, 1]`. An achromatic color has a hue of [Float.NaN], mirroring d3-color.
 */
public data class Cubehelix(
    public val h: Float,
    public val s: Float,
    public val l: Float,
    public val opacity: Float = 1f,
) {
    /** Returns a brighter copy of this color by scaling lightness. */
    public fun brighter(k: Float = 1f): Cubehelix = Cubehelix(h, s, l * (1f / DARKER).pow(k), opacity)

    /** Returns a darker copy of this color by scaling lightness. */
    public fun darker(k: Float = 1f): Cubehelix = Cubehelix(h, s, l * DARKER.pow(k), opacity)
}

/** Converts this ARGB color to the Cubehelix color space. */
public fun Color.toCubehelix(): Cubehelix {
    val r = redFraction()
    val g = greenFraction()
    val b = blueFraction()
    val l = (BC_DA * b + ED * r - EB * g) / (BC_DA + ED - EB)
    val bl = b - l
    val k = (E * (g - l) - C * bl) / D
    val s = sqrt(k * k + bl * bl) / (E * l * (1f - l))
    val h = if (s != 0f && !s.isNaN()) {
        (atan2(k, bl) * DEGREES - 120f).let { if (it < 0f) it + 360f else it }
    } else {
        Float.NaN
    }
    return Cubehelix(h, s, l, opacityFraction())
}

/** Converts this Cubehelix color to an ARGB [Color]. */
public fun Cubehelix.toColor(): Color {
    val hr = if (h.isNaN()) 0f else (h + 120f) * RADIANS
    val a = if (s.isNaN()) 0f else s * l * (1f - l)
    val cosh = cos(hr)
    val sinh = sin(hr)
    return Color(
        alpha = opacityToAlpha(opacity),
        red = channelToInt(255f * (l + a * (A * cosh + B * sinh))),
        green = channelToInt(255f * (l + a * (C * cosh + D * sinh))),
        blue = channelToInt(255f * (l + a * (E * cosh))),
    )
}
