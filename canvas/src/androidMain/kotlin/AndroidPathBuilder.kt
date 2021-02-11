package com.juul.krayon.canvas

import android.graphics.Path as AndroidPath

/** Create an Android [Path][AndroidPath] from path builder actions. */
inline fun androidPath(builder: (PathBuilder<*>) -> Unit): AndroidPath =
    AndroidPathBuilder().apply(builder).build()

@PublishedApi
internal class AndroidPathBuilder : PathBuilder<AndroidPath> {
    private val pathBuffer = android.graphics.Path()

    override fun moveTo(x: Float, y: Float) {
        pathBuffer.moveTo(x, y)
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        pathBuffer.rMoveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        pathBuffer.lineTo(x, y)
    }

    override fun relativeLineTo(x: Float, y: Float) {
        pathBuffer.rLineTo(x, y)
    }

    override fun arcTo(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        forceMoveTo: Boolean,
    ) {
        pathBuffer.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
    }

    override fun quadraticTo(
        controlX: Float,
        controlY: Float,
        endX: Float,
        endY: Float,
    ) {
        pathBuffer.quadTo(controlX, controlY, endX, endY)
    }

    override fun relativeQuadraticTo(
        controlX: Float,
        controlY: Float,
        endX: Float,
        endY: Float,
    ) {
        pathBuffer.rQuadTo(controlX, controlY, endX, endY)
    }

    override fun cubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ) {
        pathBuffer.cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun relativeCubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ) {
        pathBuffer.rCubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun close() {
        pathBuffer.close()
    }

    override fun reset() {
        pathBuffer.reset()
    }

    override fun build(): AndroidPath = AndroidPath(pathBuffer)
}
