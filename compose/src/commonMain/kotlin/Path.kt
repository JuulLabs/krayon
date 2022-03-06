package com.juul.krayon.compose

import androidx.compose.ui.geometry.Rect
import com.juul.krayon.kanvas.PathBuilder
import androidx.compose.ui.graphics.Path as ComposePath

private val EMPTY_PATH = ComposePath()

@PublishedApi
internal class ComposePathBuilder : PathBuilder<ComposePath> {
    private val pathBuffer = ComposePath()

    override fun moveTo(x: Float, y: Float) {
        pathBuffer.moveTo(x, y)
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        pathBuffer.relativeMoveTo(x, y)
    }

    override fun lineTo(x: Float, y: Float) {
        pathBuffer.lineTo(x, y)
    }

    override fun relativeLineTo(x: Float, y: Float) {
        pathBuffer.relativeLineTo(x, y)
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
        pathBuffer.arcTo(Rect(left, top, right, bottom), startAngle, sweepAngle, forceMoveTo)
    }

    override fun quadraticTo(
        controlX: Float,
        controlY: Float,
        endX: Float,
        endY: Float,
    ) {
        pathBuffer.quadraticBezierTo(controlX, controlY, endX, endY)
    }

    override fun relativeQuadraticTo(
        controlX: Float,
        controlY: Float,
        endX: Float,
        endY: Float,
    ) {
        pathBuffer.relativeQuadraticBezierTo(controlX, controlY, endX, endY)
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
        pathBuffer.relativeCubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
    }

    override fun close() {
        pathBuffer.close()
    }

    override fun reset() {
        pathBuffer.reset()
    }

    override fun build(): ComposePath = pathBuffer
}
