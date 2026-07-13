package com.juul.krayon.color

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

/**
 * A color in the CIELCh (HCL) color space — the polar form of [Lab].
 *
 * [h] is hue in degrees `[0, 360)`, [c] is chroma, [l] is lightness, and [opacity] is in `[0, 1]`.
 * An achromatic color has a hue of [Float.NaN], mirroring d3-color.
 */
public data class Hcl(
    public val h: Float,
    public val c: Float,
    public val l: Float,
    public val opacity: Float = 1f,
) {
    /** Returns a brighter copy of this color by increasing lightness. */
    public fun brighter(k: Float = 1f): Hcl = toLab().brighter(k).toHcl()

    /** Returns a darker copy of this color by decreasing lightness. */
    public fun darker(k: Float = 1f): Hcl = toLab().darker(k).toHcl()
}

/** Creates an [Hcl] color using the LCh channel ordering (lightness, chroma, hue). */
public fun lch(l: Float, c: Float, h: Float, opacity: Float = 1f): Hcl = Hcl(h, c, l, opacity)

/** Converts this CIELAB color to the HCL color space. */
public fun Lab.toHcl(): Hcl {
    val h = if (a == 0f && b == 0f) {
        Float.NaN
    } else {
        (atan2(b, a) * DEGREES).let { if (it < 0f) it + 360f else it }
    }
    return Hcl(h, hypot(a, b), l, opacity)
}

/** Converts this HCL color to the CIELAB color space. */
public fun Hcl.toLab(): Lab {
    if (h.isNaN()) return Lab(l, 0f, 0f, opacity)
    val hr = h * RADIANS
    return Lab(l, cos(hr) * c, sin(hr) * c, opacity)
}

/** Converts this ARGB color to the HCL color space. */
public fun Color.toHcl(): Hcl = toLab().toHcl()

/** Converts this HCL color to an ARGB [Color]. */
public fun Hcl.toColor(): Color = toLab().toColor()
