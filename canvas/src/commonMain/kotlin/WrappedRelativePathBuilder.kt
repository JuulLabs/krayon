package com.juul.krayon.canvas

public abstract class WrappedRelativePathBuilder<P> : PathBuilder<P> {

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

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        updatePosition(endX, endY)
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        quadraticTo(lastX + controlX, lastY + controlX, lastX + endX, lastY + endY)
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
}
