package com.juul.krayon.canvas

import org.w3c.dom.Path2D

public class Path2DBuilder : WrappedRelativePathBuilder<Path2D>() {
    private var buffer = Path2D()

    override fun moveTo(x: Float, y: Float) {
        super.moveTo(x, y)
        buffer.moveTo(x.toDouble(), y.toDouble())
    }

    override fun lineTo(x: Float, y: Float) {
        super.lineTo(x, y)
        buffer.lineTo(x.toDouble(), y.toDouble())
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
