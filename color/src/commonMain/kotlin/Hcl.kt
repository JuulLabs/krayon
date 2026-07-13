package com.juul.krayon.color

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * A color in the CIELCh (HCL) color space — the polar form of [Lab].
 *
 * [hue] is in degrees `[0, 360)`, [chroma] is the distance from the neutral axis, [lightness] is
 * the L\* axis, and [opacity] is in `[0, 1]`. An achromatic color has a hue of [Float.NaN],
 * mirroring d3-color.
 */
public data class Hcl(
    public val hue: Float,
    public val chroma: Float,
    public val lightness: Float,
    public val opacity: Float = 1f,
) {
    /** Returns a brighter copy of this color by increasing lightness. */
    public fun brighter(k: Float = 1f): Hcl = toLab().brighter(k).toHcl()

    /** Returns a darker copy of this color by decreasing lightness. */
    public fun darker(k: Float = 1f): Hcl = toLab().darker(k).toHcl()
}

/** Creates an [Hcl] color using the LCh channel ordering (lightness, chroma, hue). */
public fun lch(lightness: Float, chroma: Float, hue: Float, opacity: Float = 1f): Hcl =
    Hcl(hue, chroma, lightness, opacity)

/** Converts this CIELAB color to the HCL color space. */
public fun Lab.toHcl(): Hcl {
    val hue = if (a == 0f && b == 0f) {
        Float.NaN
    } else {
        (atan2(b, a) * DEGREES).let { if (it < 0f) it + 360f else it }
    }
    return Hcl(hue, hypot(a, b), lightness, opacity)
}

/** Converts this HCL color to the CIELAB color space. */
public fun Hcl.toLab(): Lab {
    if (hue.isNaN()) return Lab(lightness, 0f, 0f, opacity)
    val hueRadians = hue * RADIANS
    return Lab(lightness, cos(hueRadians) * chroma, sin(hueRadians) * chroma, opacity)
}

/** Converts this ARGB color to the HCL color space. */
public fun Color.toHcl(): Hcl = toLab().toHcl()

/** Converts this HCL color to an ARGB [Color]. */
public fun Hcl.toColor(): Color = toLab().toColor()
