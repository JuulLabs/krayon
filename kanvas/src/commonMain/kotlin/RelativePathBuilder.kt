package com.juul.krayon.kanvas

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
        // TODO: Benchmark this. There's a lot of duplicated math between the calls to getEllipseX/Y. Might make more
        //       sense to have an `Ellipse` class that can cache intermediate results (less math, but an extra allocation).
        val startX = getEllipseX(left, top, right, bottom, startAngle)
        val startY = getEllipseY(left, top, right, bottom, startAngle)
        if (startX != lastX || startY != lastY) {
            if (forceMoveTo) {
                moveTo(startX, startY)
            } else {
                lineTo(startX, startY)
            }
        }

        val endX = getEllipseX(left, top, right, bottom, startAngle + sweepAngle)
        val endY = getEllipseY(left, top, right, bottom, startAngle + sweepAngle)
        updatePosition(endX, endY)
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
