package com.juul.krayon.interpolate

import com.juul.krayon.color.Color
import kotlin.math.floor

private fun basis(t1: Float, v0: Float, v1: Float, v2: Float, v3: Float): Float {
    val t2 = t1 * t1
    val t3 = t2 * t1
    return (
        (1f - 3f * t1 + 3f * t2 - t3) * v0 +
            (4f - 6f * t2 + 3f * t3) * v1 +
            (1f + 3f * t1 + 3f * t2 - 3f * t3) * v2 +
            t3 * v3
    ) / 6f
}

/**
 * Interpolates a uniform, non-rational B-spline through the given control [values]. The result
 * passes through the first and last values at `t == 0` and `t == 1`.
 */
public fun interpolateBasis(values: List<Float>): Interpolator<Float> {
    require(values.isNotEmpty()) { "At least one value is required." }
    val n = values.size - 1
    return FunctionInterpolator { fraction ->
        val i: Int
        val t: Float
        when {
            fraction <= 0f -> {
                t = 0f
                i = 0
            }
            fraction >= 1f -> {
                t = 1f
                i = if (n > 0) n - 1 else 0
            }
            else -> {
                t = fraction
                i = floor(t * n).toInt()
            }
        }
        val v1 = values[i]
        val v2 = values[if (i + 1 <= n) i + 1 else i]
        val v0 = if (i > 0) values[i - 1] else 2f * v1 - v2
        val v3 = if (i < n - 1) values[i + 2] else 2f * v2 - v1
        basis((t - i.toFloat() / n) * n, v0, v1, v2, v3)
    }
}

/** Like [interpolateBasis], but the control [values] wrap around to form a closed loop. */
public fun interpolateBasisClosed(values: List<Float>): Interpolator<Float> {
    require(values.isNotEmpty()) { "At least one value is required." }
    val n = values.size
    return FunctionInterpolator { fraction ->
        var t = fraction % 1f
        if (t < 0f) t += 1f
        val i = floor(t * n).toInt()
        val v0 = values[(i + n - 1) % n]
        val v1 = values[i % n]
        val v2 = values[(i + 1) % n]
        val v3 = values[(i + 2) % n]
        basis((t - i.toFloat() / n) * n, v0, v1, v2, v3)
    }
}

/** Interpolates through the given [colors] using a B-spline over each RGB channel. */
public fun interpolateRgbBasis(colors: List<Color>): Interpolator<Color> =
    rgbSpline(colors, ::interpolateBasis)

/** Like [interpolateRgbBasis], but the [colors] wrap around to form a closed loop. */
public fun interpolateRgbBasisClosed(colors: List<Color>): Interpolator<Color> =
    rgbSpline(colors, ::interpolateBasisClosed)

private fun rgbSpline(colors: List<Color>, spline: (List<Float>) -> Interpolator<Float>): Interpolator<Color> {
    val r = spline(colors.map { it.red.toFloat() })
    val g = spline(colors.map { it.green.toFloat() })
    val b = spline(colors.map { it.blue.toFloat() })
    return FunctionInterpolator { t -> buildColor(r.interpolate(t), g.interpolate(t), b.interpolate(t), 1f) }
}
