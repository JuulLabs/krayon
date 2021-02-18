package com.juul.krayon.canvas.svg

import com.juul.krayon.canvas.PathBuilder
import com.juul.krayon.canvas.getEllipseX
import com.juul.krayon.canvas.getEllipseY
import kotlin.math.abs

public inline class PathString(public val string: String)

internal class PathStringBuilder : PathBuilder<PathString> {
    private val buffer = StringBuilder()

    private fun separate() {
        if (buffer.isNotEmpty()) {
            buffer.append(' ')
        }
    }

    override fun moveTo(x: Float, y: Float) {
        separate()
        buffer.append("M $x $y")
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        separate()
        buffer.append("m $x $y")
    }

    override fun lineTo(x: Float, y: Float) {
        separate()
        buffer.append("L $x $y")
    }

    override fun relativeLineTo(x: Float, y: Float) {
        separate()
        buffer.append("l $x $y")
    }

    override fun arcTo(left: Float, top: Float, right: Float, bottom: Float, startAngle: Float, sweepAngle: Float, forceMoveTo: Boolean) {
        separate()
        val startX = getEllipseX(left, top, right, bottom, startAngle)
        val startY = getEllipseY(left, top, right, bottom, startAngle)
        if (forceMoveTo || buffer.isEmpty()) {
            moveTo(startX, startY)
        } else {
            lineTo(startX, startY)
        }

        separate()
        val rx = (right - left) / 2f
        val ry = (bottom - top) / 2f
        val sweep = if (sweepAngle >= 0f) 1 else 0
        val large = if (abs(sweepAngle) > 180f) 1 else 0
        val endX = getEllipseX(left, top, right, bottom, startAngle + sweepAngle)
        val endY = getEllipseY(left, top, right, bottom, startAngle + sweepAngle)
        buffer.append("A $rx $ry 0 $large $sweep $endX $endY")
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        separate()
        buffer.append("Q $controlX $controlY $endX $endY")
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        separate()
        buffer.append("q $controlX $controlY $endX $endY")
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        separate()
        buffer.append("C $beginControlX $beginControlY $endControlX $endControlY $endX $endY")
    }

    override fun relativeCubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ) {
        separate()
        buffer.append("c $beginControlX $beginControlY $endControlX $endControlY $endX $endY")
    }

    override fun close() {
        separate()
        buffer.append('z')
    }

    override fun reset() {
        buffer.clear()
    }

    override fun build(): PathString = PathString(buffer.toString())
}
