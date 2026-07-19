package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN

/** B-spline curve. Ported from [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/basis.js). */
public object Basis : Curve {
    private var x0: Float = NaN
    private var y0: Float = NaN
    private var x1: Float = NaN
    private var y1: Float = NaN
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
        point = 0
    }

    override fun endLine(context: PathBuilder<*>) {
        when (point) {
            3 -> {
                basisPoint(context, x0, y0, x1, y1, x1, y1)
                context.lineTo(x1, y1)
            }
            2 -> context.lineTo(x1, y1)
        }
        if (line.truthy() || (line != 0f && point == 1)) context.close()
        line = 1f - line
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        when (point) {
            0 -> {
                point = 1
                if (line.truthy()) context.lineTo(x, y) else context.moveTo(x, y)
            }
            1 -> point = 2
            2 -> {
                point = 3
                context.lineTo((5 * x0 + x1) / 6, (5 * y0 + y1) / 6)
                basisPoint(context, x0, y0, x1, y1, x, y)
            }
            else -> basisPoint(context, x0, y0, x1, y1, x, y)
        }
        x0 = x1
        x1 = x
        y0 = y1
        y1 = y
    }
}
