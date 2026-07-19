package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN
import kotlin.math.pow
import kotlin.math.sqrt

internal fun catmullRomPoint(
    context: PathBuilder<*>,
    x0: Float,
    y0: Float,
    x1: Float,
    y1: Float,
    x2: Float,
    y2: Float,
    l01a: Float,
    l012a: Float,
    l12a: Float,
    l122a: Float,
    l23a: Float,
    l232a: Float,
    x: Float,
    y: Float,
) {
    var cx1 = x1
    var cy1 = y1
    var cx2 = x2
    var cy2 = y2

    if (l01a > EPSILON) {
        val a = 2 * l012a + 3 * l01a * l12a + l122a
        val n = 3 * l01a * (l01a + l12a)
        cx1 = (x1 * a - x0 * l122a + x2 * l012a) / n
        cy1 = (y1 * a - y0 * l122a + y2 * l012a) / n
    }

    if (l23a > EPSILON) {
        val b = 2 * l232a + 3 * l23a * l12a + l122a
        val m = 3 * l23a * (l23a + l12a)
        cx2 = (x2 * b + x1 * l232a - x * l122a) / m
        cy2 = (y2 * b + y1 * l232a - y * l122a) / m
    }

    context.cubicTo(cx1, cy1, cx2, cy2, x2, y2)
}

/**
 * Catmull–Rom spline curve. The [alpha] (default `0.5`, centripetal) controls the parameterization.
 * An [alpha] of `0` degenerates to a uniform [Cardinal] spline, matching
 * [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/catmullRom.js).
 */
public class CatmullRom(alpha: Float = 0.5f) : Curve by (if (alpha != 0f) CatmullRomCurve(alpha) else Cardinal(0f))

internal class CatmullRomCurve(private val alpha: Float) : Curve {
    private var x0: Float = NaN
    private var y0: Float = NaN
    private var x1: Float = NaN
    private var y1: Float = NaN
    private var x2: Float = NaN
    private var y2: Float = NaN
    private var l01a: Float = 0f
    private var l12a: Float = 0f
    private var l23a: Float = 0f
    private var l012a: Float = 0f
    private var l122a: Float = 0f
    private var l232a: Float = 0f
    private var line: Float = NaN
    private var point: Int = 0

    override fun startArea(context: PathBuilder<*>) {
        line = 0f
    }

    override fun endArea(context: PathBuilder<*>) {
        line = NaN
    }

    override fun startLine(context: PathBuilder<*>) {
        x0 = NaN
        y0 = NaN
        x1 = NaN
        y1 = NaN
        x2 = NaN
        y2 = NaN
        l01a = 0f
        l12a = 0f
        l23a = 0f
        l012a = 0f
        l122a = 0f
        l232a = 0f
        point = 0
    }

    override fun endLine(context: PathBuilder<*>) {
        when (point) {
            2 -> context.lineTo(x2, y2)
            3 -> point(context, x2, y2)
        }
        if (line.truthy() || (line != 0f && point == 1)) context.close()
        line = 1f - line
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        if (point != 0) {
            val x23 = x2 - x
            val y23 = y2 - y
            l232a = (x23 * x23 + y23 * y23).pow(alpha)
            l23a = sqrt(l232a)
        }

        when (point) {
            0 -> {
                point = 1
                if (line.truthy()) context.lineTo(x, y) else context.moveTo(x, y)
            }
            1 -> point = 2
            2 -> {
                point = 3
                catmullRomPoint(context, x0, y0, x1, y1, x2, y2, l01a, l012a, l12a, l122a, l23a, l232a, x, y)
            }
            else -> catmullRomPoint(context, x0, y0, x1, y1, x2, y2, l01a, l012a, l12a, l122a, l23a, l232a, x, y)
        }

        l01a = l12a
        l12a = l23a
        l012a = l122a
        l122a = l232a
        x0 = x1
        x1 = x2
        x2 = x
        y0 = y1
        y1 = y2
        y2 = y
    }
}
