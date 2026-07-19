package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN

/** Closed B-spline curve. Ported from [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/basisClosed.js). */
public object BasisClosed : Curve {
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
        point = 0
    }

    override fun endLine(context: PathBuilder<*>) {
        when (point) {
            1 -> {
                context.moveTo(x2, y2)
                context.close()
            }
            2 -> {
                context.moveTo((x2 + 2 * x3) / 3, (y2 + 2 * y3) / 3)
                context.lineTo((x3 + 2 * x2) / 3, (y3 + 2 * y2) / 3)
                context.close()
            }
            3 -> {
                point(context, x2, y2)
                point(context, x3, y3)
                point(context, x4, y4)
            }
        }
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        when (point) {
            0 -> {
                point = 1
                x2 = x
                y2 = y
            }
            1 -> {
                point = 2
                x3 = x
                y3 = y
            }
            2 -> {
                point = 3
                x4 = x
                y4 = y
                context.moveTo((x0 + 4 * x1 + x) / 6, (y0 + 4 * y1 + y) / 6)
            }
            else -> basisPoint(context, x0, y0, x1, y1, x, y)
        }
        x0 = x1
        x1 = x
        y0 = y1
        y1 = y
    }
}
