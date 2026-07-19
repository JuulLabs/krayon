package com.juul.krayon.interpolate

import kotlin.math.roundToInt

/** Interpolates linearly between [start] and [stop], rounding each result to the nearest integer. */
public fun interpolateRound(start: Float, stop: Float): Interpolator<Int> =
    FunctionInterpolator { t -> (start * (1f - t) + stop * t).roundToInt() }

/** Interpolates linearly between [start] and [stop], rounding each result to the nearest integer. */
public fun interpolateRound(start: Double, stop: Double): Interpolator<Int> =
    FunctionInterpolator { t -> (start * (1.0 - t) + stop * t).roundToInt() }
