package com.juul.krayon.color

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin
import kotlin.math.sqrt

// Coefficients of Dave Green's Cubehelix scheme, matching d3-color. Each pair scales the cosine and
// sine of the (rotated) hue when reconstructing a channel; blue has no sine term.
// https://www.mrao.cam.ac.uk/~dag/CUBEHELIX/
private const val RED_COSINE = -0.14861f
private const val RED_SINE = 1.78277f
private const val GREEN_COSINE = -0.29227f
private const val GREEN_SINE = -0.90649f
private const val BLUE_COSINE = 1.97294f

// Precomputed products used to solve for lightness when converting RGB to Cubehelix.
private const val BLUE_GREEN_SINE = BLUE_COSINE * GREEN_SINE
private const val BLUE_RED_SINE = BLUE_COSINE * RED_SINE
private const val RED_GREEN_CROSS = RED_SINE * GREEN_COSINE - GREEN_SINE * RED_COSINE

/**
 * A color in Dave Green's Cubehelix color space.
 *
 * [hue] is in degrees, [saturation] is unbounded but typically in `[0, 2]`, [lightness] is in
 * `[0, 1]`, and [opacity] is in `[0, 1]`. An achromatic color has a hue of [Float.NaN], mirroring
 * d3-color.
 */
public data class Cubehelix(
    public val hue: Float,
    public val saturation: Float,
    public val lightness: Float,
    public val opacity: Float = 1f,
) {
    /** Returns a brighter copy of this color by scaling lightness. */
    public fun brighter(k: Float = 1f): Cubehelix =
        Cubehelix(hue, saturation, lightness * (1f / DARKER).pow(k), opacity)

    /** Returns a darker copy of this color by scaling lightness. */
    public fun darker(k: Float = 1f): Cubehelix =
        Cubehelix(hue, saturation, lightness * DARKER.pow(k), opacity)
}

/** Converts this ARGB color to the Cubehelix color space. */
public fun Color.toCubehelix(): Cubehelix {
    val red = redFraction()
    val green = greenFraction()
    val blue = blueFraction()
    val lightness =
        (RED_GREEN_CROSS * blue + BLUE_GREEN_SINE * red - BLUE_RED_SINE * green) /
            (RED_GREEN_CROSS + BLUE_GREEN_SINE - BLUE_RED_SINE)
    val blueMinusLightness = blue - lightness
    val sineComponent = (BLUE_COSINE * (green - lightness) - GREEN_COSINE * blueMinusLightness) / GREEN_SINE
    val saturation =
        sqrt(sineComponent * sineComponent + blueMinusLightness * blueMinusLightness) /
            (BLUE_COSINE * lightness * (1f - lightness))
    val hue = if (saturation != 0f && !saturation.isNaN()) {
        (atan2(sineComponent, blueMinusLightness) * DEGREES - 120f).let { if (it < 0f) it + 360f else it }
    } else {
        Float.NaN
    }
    return Cubehelix(hue, saturation, lightness, opacityFraction())
}

/** Converts this Cubehelix color to an ARGB [Color]. */
public fun Cubehelix.toColor(): Color {
    val hueRadians = if (hue.isNaN()) 0f else (hue + 120f) * RADIANS
    val amplitude = if (saturation.isNaN()) 0f else saturation * lightness * (1f - lightness)
    val cosHue = cos(hueRadians)
    val sinHue = sin(hueRadians)
    return Color(
        alpha = opacityToAlpha(opacity),
        red = channelToInt(255f * (lightness + amplitude * (RED_COSINE * cosHue + RED_SINE * sinHue))),
        green = channelToInt(255f * (lightness + amplitude * (GREEN_COSINE * cosHue + GREEN_SINE * sinHue))),
        blue = channelToInt(255f * (lightness + amplitude * (BLUE_COSINE * cosHue))),
    )
}
