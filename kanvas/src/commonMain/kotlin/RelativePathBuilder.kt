package com.juul.krayon.kanvas

import kotlin.math.PI
import kotlin.math.pow
import kotlin.math.sqrt
import kotlin.math.tan

/** Handles relative path building when the underlying output type can't. */
public abstract class RelativePathBuilder<P> : PathBuilder<P> {

    private var closeToX: Float = 0f
    private var closeToY: Float = 0f

    private var lastX: Float = 0f
    private var lastY: Float = 0f

    private fun updatePosition(x: Float, y: Float) {
        lastX = x
        lastY = y
    }

    override fun moveTo(x: Float, y: Float) {
        updatePosition(x, y)
        closeToX = x
        closeToY = y
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        moveTo(lastX + x, lastY + y)
    }

    override fun lineTo(x: Float, y: Float) {
        updatePosition(x, y)
    }

    override fun relativeLineTo(x: Float, y: Float) {
        lineTo(lastX + x, lastY + y)
    }

    override fun arcTo(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, forceMoveTo: Boolean) {
        // FIXME: Implement `forceMoveTo` correctly.
        // TODO: Test this equation from math.stackexchange.com, and see about optimizing.
        // https://math.stackexchange.com/questions/22064/calculating-a-point-that-lies-on-an-ellipse-given-an-anglev
        val a = (right - left) / 2.0
        val b = (bottom - top) / 2.0
        val centerX = left + a
        val centerY = top + b
        val theta = (startAngle + sweepAngle) * PI / 180f
        val signX = if (theta.rem(2 * PI) !in (PI / 2)..(PI * 3 / 2)) 1 else -1
        val signY = if (theta.rem(2 * PI) in 0.0..PI) 1 else -1
        val dX = signX * a * b / sqrt(b.pow(2) + a.pow(2) * tan(theta).pow(2))
        val dY = signY * a * b / sqrt(a.pow(2) + b.pow(2) / tan(theta).pow(2))
        updatePosition((centerX + dX).toFloat(), (centerY + dY).toFloat())
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        updatePosition(endX, endY)
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        quadraticTo(lastX + controlX, lastY + controlY, lastX + endX, lastY + endY)
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        updatePosition(endX, endY)
    }

    override fun relativeCubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ) {
        cubicTo(
            lastX + beginControlX,
            lastY + beginControlY,
            lastX + endControlX,
            lastY + endControlY,
            lastX + endX,
            lastY + endY
        )
    }

    override fun close() {
        updatePosition(closeToX, closeToY)
    }

    override fun reset() {
        lastX = 0f
        lastY = 0f
        closeToX = 0f
        closeToY = 0f
    }

    /** No @TestOnly or @VisibleForTesting or anything in Kotlin Multiplatform. */
    internal fun getTestState() = TestState(closeToX, closeToY, lastX, lastY)

    /** No @TestOnly or @VisibleForTesting or anything in Kotlin Multiplatform. */
    internal data class TestState(
        val closeToX: Float = 0f,
        val closeToY: Float = 0f,
        val lastX: Float = 0f,
        val lastY: Float = 0f,
    )
}
