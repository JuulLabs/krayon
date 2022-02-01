package com.juul.krayon.kanvas

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

// TODO: Test these equations from math.stackexchange.com, and see about optimizing.
// https://math.stackexchange.com/questions/22064/calculating-a-point-that-lies-on-an-ellipse-given-an-anglev

/** Angle in degrees. */
internal fun getEllipseX(left: Float, top: Float, right: Float, bottom: Float, angle: Float): Float {
    val a = (right - left) / 2.0
    val b = (bottom - top) / 2.0
    val centerX = left + a
    val theta = angle * PI / 180f
    val signX = if (angle.normalizeDegrees() in 90f..270f) -1 else 1
    val dX = signX * a * b / sqrt(b.pow(2) + a.pow(2) * tan(theta).pow(2))
    return (centerX + dX).toFloat()
}

/** Angle in degrees. */
internal fun getEllipseY(left: Float, top: Float, right: Float, bottom: Float, angle: Float): Float {
    val a = (right - left) / 2.0
    val b = (bottom - top) / 2.0
    val centerY = top + b
    val theta = angle * PI / 180f
    val signY = if (angle.normalizeDegrees() in 0f..180f) 1 else -1
    val dY = signY * a * b / sqrt(a.pow(2) + b.pow(2) / tan(theta).pow(2))
    return (centerY + dY).toFloat()
}

private fun Float.normalizeDegrees(): Float {
    val remainder = this % 360
    return if (remainder < 0f) remainder + 360f else remainder
}
