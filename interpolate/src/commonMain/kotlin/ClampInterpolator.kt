package com.juul.krayon.interpolate

/** Returns an [Interpolator] that wraps `this` such that input fractions are coerced to the range `0f..1f`. */
public fun <T> Interpolator<T>.clamp(): Interpolator<T> = ClampInterpolator(this)

private class ClampInterpolator<T>(
    private val delegate: Interpolator<T>,
) : Interpolator<T> {
    override fun interpolate(fraction: Float): T = delegate.interpolate(fraction.coerceIn(0f, 1f))
}
