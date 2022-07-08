package com.juul.krayon.kanvas.svg

import com.juul.krayon.kanvas.PathBuilder
import com.juul.krayon.kanvas.getEllipseX
import com.juul.krayon.kanvas.getEllipseY
import com.juul.krayon.kanvas.xml.NumberFormatter
import kotlin.math.abs

internal class PathStringBuilder(private val fmt: NumberFormatter) : PathBuilder<PathString> {
    private val buffer = StringBuilder()

    private fun separate() {
        if (buffer.isNotEmpty()) {
            buffer.append(' ')
        }
    }

    override fun moveTo(x: Float, y: Float) {
        separate()
        buffer.append("M ${fmt(x)} ${fmt(y)}")
    }

    override fun relativeMoveTo(x: Float, y: Float) {
        separate()
        buffer.append("m ${fmt(x)} ${fmt(y)}")
    }

    override fun lineTo(x: Float, y: Float) {
        separate()
        buffer.append("L ${fmt(x)} ${fmt(y)}")
    }

    override fun relativeLineTo(x: Float, y: Float) {
        separate()
        buffer.append("l ${fmt(x)} ${fmt(y)}")
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
        buffer.append("A ${fmt(rx)} ${fmt(ry)} 0 $large $sweep ${fmt(endX)} ${fmt(endY)}")
    }

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        separate()
        buffer.append("Q ${fmt(controlX)} ${fmt(controlY)} ${fmt(endX)} ${fmt(endY)}")
    }

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float) {
        separate()
        buffer.append("q ${fmt(controlX)} ${fmt(controlY)} ${fmt(endX)} ${fmt(endY)}")
    }

    override fun cubicTo(beginControlX: Float, beginControlY: Float, endControlX: Float, endControlY: Float, endX: Float, endY: Float) {
        separate()
        buffer.append("C ${fmt(beginControlX)} ${fmt(beginControlY)} ${fmt(endControlX)} ${fmt(endControlY)} ${fmt(endX)} ${fmt(endY)}")
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
        buffer.append("c ${fmt(beginControlX)} ${fmt(beginControlY)} ${fmt(endControlX)} ${fmt(endControlY)} ${fmt(endX)} ${fmt(endY)}")
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
