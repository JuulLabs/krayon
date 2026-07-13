package com.juul.krayon.interpolate

import com.juul.krayon.color.Color
import kotlin.math.pow
import kotlin.math.round
import kotlin.math.roundToInt

internal class FunctionInterpolator<T>(
    private val block: (Float) -> T,
) : Interpolator<T> {
    override fun interpolate(fraction: Float): T = block(fraction)
}

internal fun interpolateChannel(a: Float, b: Float): (Float) -> Float {
    val d = b - a
    return if (d != 0f && !d.isNaN()) {
        { t -> a + t * d }
    } else {
        val c = if (a.isNaN()) b else a
        { c }
    }
}

internal fun gammaChannel(a: Float, b: Float, gamma: Float): (Float) -> Float {
    if (gamma == 1f) return interpolateChannel(a, b)
    val d = b - a
    if (d == 0f || d.isNaN()) {
        val c = if (a.isNaN()) b else a
        return { c }
    }
    val aa = a.pow(gamma)
    val bb = b.pow(gamma) - aa
    val yi = 1f / gamma
    return { t -> (aa + t * bb).pow(yi) }
}

internal fun interpolateHue(a: Float, b: Float): (Float) -> Float {
    val d = b - a
    return if (d != 0f && !d.isNaN()) {
        val dd = if (d > 180f || d < -180f) d - 360f * round(d / 360f) else d
        { t -> a + t * dd }
    } else {
        val c = if (a.isNaN()) b else a
        { c }
    }
}

internal fun buildColor(red: Float, green: Float, blue: Float, opacity: Float): Color = Color(
    alpha = (opacity.coerceIn(0f, 1f) * 255f).roundToInt(),
    red = red.roundToInt().coerceIn(0, 255),
    green = green.roundToInt().coerceIn(0, 255),
    blue = blue.roundToInt().coerceIn(0, 255),
)
