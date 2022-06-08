package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN
import kotlin.math.abs
import kotlin.math.sign

/** See [equivalent D3 curve](https://github.com/d3/d3-shape#curveMonotoneX). */
public class MonotoneX : Curve {
    private var point: Int = 0
    private var line: Float = NaN
    private var x0: Float = NaN
    private var y0: Float = NaN
    private var x1: Float = NaN
    private var y1: Float = NaN
    private var t0: Float = NaN

    override fun startArea(context: PathBuilder<*>) {
        line = 0f
    }

    override fun endArea(context: PathBuilder<*>) {
        line = NaN
    }

    override fun startLine(context: PathBuilder<*>) {
        x0 = NaN
        x0 = NaN
        y1 = NaN
        y1 = NaN
        t0 = NaN
        point = 0
    }

    override fun endLine(context: PathBuilder<*>) {
        if (point == 2) {
            context.lineTo(x1, y1)
        } else if (point == 3) {
            curveToPoint(context, t0, slope2(t0))
        }
        if (line.truthy() || (line != 0f && point == 1)) {
            context.close()
        }
        line = 1f - line
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        var t1 = NaN
        if (x == x1 && y == y1) return // Ignore coincident points.
        if (point < 3) {
            if (point == 0) {
                if (line.truthy()) {
                    context.lineTo(x, y)
                } else {
                    context.moveTo(x, y)
                }
            } else if (point == 2) {
                t1 = slope3(x, y)
                curveToPoint(context, slope2(t1), t1)
            }
            point += 1
        } else {
            t1 = slope3(x, y)
            curveToPoint(context, t0, t1)
        }

        x0 = x1
        x1 = x
        y0 = y1
        y1 = y
        t0 = t1
    }

    private fun curveToPoint(context: PathBuilder<*>, t0: Float, t1: Float) {
        val dx = (x1 - x0) / 3
        context.cubicTo(
            beginControlX = x0 + dx, beginControlY = y0 + dx * t0,
            endControlX = x1 - dx, endControlY = y1 - dx * t1,
            endX = x1, endY = y1
        )
    }

    private fun slope2(t: Float): Float {
        val h = x1 - x0
        return if (h.truthy()) {
            ((3 * (y1 - y0) / h) - t) / 2
        } else {
            t
        }
    }

    private fun slope3(x2: Float, y2: Float): Float {
        val h0 = x1 - x0
        val h1 = x2 - x1
        val s0 = when {
            h0.truthy() -> (y1 - y0) / h0
            h1 < 0 -> Float.NEGATIVE_INFINITY
            else -> Float.POSITIVE_INFINITY
        }
        val s1 = when {
            h1.truthy() -> (y2 - y1) / h1
            h0 < 0 -> Float.NEGATIVE_INFINITY
            else -> Float.POSITIVE_INFINITY
        }
        val p = (s0 * h1 + s1 * h0) / (h0 + h1)
        return (sign(s0) + sign(s1)) * (minOf(abs(s0), abs(s1), 0.5f * abs(p)).takeIf { it.truthy() } ?: 0f)
    }
}

/** Emulate JS truthiness. */
private fun Float.truthy(): Boolean = (!this.isNaN() && this != 0f)
