package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Open Catmull–Rom spline curve. An [alpha] of `0` degenerates to a uniform [CardinalOpen] spline,
 * matching [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/catmullRomOpen.js).
 */
public class CatmullRomOpen(alpha: Float = 0.5f) :
    Curve by (if (alpha != 0f) CatmullRomOpenCurve(alpha) else CardinalOpen(0f))

internal class CatmullRomOpenCurve(private val alpha: Float) : Curve {
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
        if (line.truthy() || (line != 0f && point == 3)) context.close()
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
            0 -> point = 1
            1 -> point = 2
            2 -> {
                point = 3
                if (line.truthy()) context.lineTo(x2, y2) else context.moveTo(x2, y2)
            }
            3 -> {
                point = 4
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
