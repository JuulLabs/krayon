package com.juul.krayon.kanvas.svg

import com.juul.krayon.kanvas.PathBuilder
import com.juul.krayon.kanvas.getEllipseX
import com.juul.krayon.kanvas.getEllipseY
import kotlin.math.abs

public inline class PathString(public val string: String)

internal class PathStringBuilder(private val precision: Int) : PathBuilder<PathString> {
    private val buffer = StringBuilder()

    private fun separate() {
        if (buffer.isNotEmpty()) {
            buffer.append(' ')
        }
    }

    override fun moveTo(x: Float, y: Float) {
        separate()
        buffer.append("M ${x.fmt()} ${y.fmt()}")
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        separate()
        buffer.append("m ${x.fmt()} ${y.fmt()}")
    }

    override fun lineTo(x: Float, y: Float) {
        separate()
        buffer.append("L ${x.fmt()} ${y.fmt()}")
    }

    override fun relativeLineTo(x: Float, y: Float) {
        separate()
        buffer.append("l ${x.fmt()} ${y.fmt()}")
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
        buffer.append("A ${rx.fmt()} ${ry.fmt()} 0 $large $sweep ${endX.fmt()} ${endY.fmt()}")
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        separate()
        buffer.append("Q ${controlX.fmt()} ${controlY.fmt()} ${endX.fmt()} ${endY.fmt()}")
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        separate()
        buffer.append("q ${controlX.fmt()} ${controlY.fmt()} ${endX.fmt()} ${endY.fmt()}")
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        separate()
        buffer.append("C ${beginControlX.fmt()} ${beginControlY.fmt()} ${endControlX.fmt()} ${endControlY.fmt()} ${endX.fmt()} ${endY.fmt()}")
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
        buffer.append("c ${beginControlX.fmt()} ${beginControlY.fmt()} ${endControlX.fmt()} ${endControlY.fmt()} ${endX.fmt()} ${endY.fmt()}")
    }

    override fun close() {
        separate()
        buffer.append('z')
    }

    override fun reset() {
        buffer.clear()
    }

    override fun build(): PathString = PathString(buffer.toString())

    private fun Float.fmt() = toDouble().scientificNotation(precision)
}
