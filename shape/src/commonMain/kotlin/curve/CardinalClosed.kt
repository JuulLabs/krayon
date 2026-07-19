package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN

/**
 * Closed cardinal spline curve. Ported from
 * [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/cardinalClosed.js).
 */
public class CardinalClosed(tension: Float = 0f) : Curve {
    private val k: Float = (1f - tension) / 6f
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
