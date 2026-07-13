package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN

/**
 * Open cardinal spline curve. Ported from
 * [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/cardinalOpen.js).
 */
public class CardinalOpen(tension: Float = 0f) : Curve {
    private val k: Float = (1f - tension) / 6f
    private var x0: Float = NaN
    private var y0: Float = NaN
    private var x1: Float = NaN
    private var y1: Float = NaN
    private var x2: Float = NaN
    private var y2: Float = NaN
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
        point = 0
    }

    override fun endLine(context: PathBuilder<*>) {
        if (line.truthy() || (line != 0f && point == 3)) context.close()
        line = 1f - line
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        when (point) {
            0 -> point = 1
            1 -> point = 2
            2 -> {
                point = 3
                if (line.truthy()) context.lineTo(x2, y2) else context.moveTo(x2, y2)
            }
            3 -> {
                point = 4
                cardinalPoint(context, k, x0, y0, x1, y1, x2, y2, x, y)
            }
            else -> cardinalPoint(context, k, x0, y0, x1, y1, x2, y2, x, y)
        }
        x0 = x1
        x1 = x2
        x2 = x
        y0 = y1
        y1 = y2
        y2 = y
    }
}
