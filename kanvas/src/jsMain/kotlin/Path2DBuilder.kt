package com.juul.krayon.kanvas

import org.w3c.dom.Path2D
import kotlin.math.PI

/** Converts a [Path] to an HTML Canvas platform representation. */
public fun Path.toPath2d(): Path2D = Path2DBuilder().build(this)

public class Path2DBuilder : RelativePathBuilder<Path2D>() {
    private var buffer = Path2D()

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
        buffer.moveTo(x.toDouble(), y.toDouble())
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
        buffer.lineTo(x.toDouble(), y.toDouble())
    }

    override fun arcTo(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, forceMoveTo: Boolean) {
        super.arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo)
        val radiusX = (right - left) / 2.0
        val radiusY = (bottom - top) / 2.0
        val x = left + radiusX
        val y = top + radiusY
        val startAngleRadians = startAngle * PI / 180f
        val endAngleRadians = (startAngle + sweepAngle) * PI / 180f
        val anticlockwise = sweepAngle < 0f
        buffer.ellipse(x, y, radiusX, radiusY, rotation = 0.0, startAngleRadians, endAngleRadians, anticlockwise)
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        super.quadraticTo(controlX, controlY, endX, endY)
        buffer.quadraticCurveTo(controlX.toDouble(), controlX.toDouble(), endX.toDouble(), endY.toDouble())
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        super.cubicTo(beginControlX, beginControlY, endControlX, endControlY, endX, endY)
        buffer.bezierCurveTo(
            beginControlX.toDouble(),
            beginControlY.toDouble(),
            endControlX.toDouble(),
            endControlY.toDouble(),
            endX.toDouble(),
            endY.toDouble()
        )
    }

    override fun close() {
        super.close()
        buffer.closePath()
    }

    override fun reset() {
        super.reset()
        buffer = Path2D()
    }

    override fun build(): Path2D = Path2D(buffer)
}
