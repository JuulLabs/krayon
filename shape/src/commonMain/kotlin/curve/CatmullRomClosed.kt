package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Closed Catmull–Rom spline curve. An [alpha] of `0` degenerates to a uniform [CardinalClosed]
 * spline, matching [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/catmullRomClosed.js).
 */
public class CatmullRomClosed(alpha: Float = 0.5f) :
    Curve by (if (alpha != 0f) CatmullRomClosedCurve(alpha) else CardinalClosed(0f))

internal class CatmullRomClosedCurve(private val alpha: Float) : Curve {
    private var x0: Float = NaN
    private var y0: Float = NaN
    private var x1: Float = NaN
    private var y1: Float = NaN
    private var x2: Float = NaN
    private var y2: Float = NaN
    private var x3: Float = NaN
    private var y3: Float = NaN
    private var x4: Float = NaN
    private var y4: Float = NaN
    private var x5: Float = NaN
    private var y5: Float = NaN
    private var l01a: Float = 0f
    private var l12a: Float = 0f
    private var l23a: Float = 0f
    private var l012a: Float = 0f
    private var l122a: Float = 0f
    private var l232a: Float = 0f
    private var point: Int = 0

    override fun startArea(context: PathBuilder<*>) {}

    override fun endArea(context: PathBuilder<*>) {}

    override fun startLine(context: PathBuilder<*>) {
        x0 = NaN
        y0 = NaN
        x1 = NaN
        y1 = NaN
        x2 = NaN
        y2 = NaN
        x3 = NaN
        y3 = NaN
        x4 = NaN
        y4 = NaN
        x5 = NaN
        y5 = NaN
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
            1 -> {
                context.moveTo(x3, y3)
                context.close()
            }
            2 -> {
                context.lineTo(x3, y3)
                context.close()
            }
            3 -> {
                point(context, x3, y3)
                point(context, x4, y4)
                point(context, x5, y5)
            }
        }
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
                x3 = x
                y3 = y
            }
            1 -> {
                point = 2
                x4 = x
                y4 = y
                context.moveTo(x4, y4)
            }
            2 -> {
                point = 3
                x5 = x
                y5 = y
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
