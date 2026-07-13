package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN

/** Bump curve. Ported from [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/bump.js). */
internal class Bump(private val isX: Boolean) : Curve {
    private var x0: Float = NaN
    private var y0: Float = NaN
    private var line: Float = NaN
    private var point: Int = 0

    override fun startArea(context: PathBuilder<*>) {
        line = 0f
    }

    override fun endArea(context: PathBuilder<*>) {
        line = NaN
    }

    override fun startLine(context: PathBuilder<*>) {
        point = 0
    }

    override fun endLine(context: PathBuilder<*>) {
        if (line.truthy() || (line != 0f && point == 1)) context.close()
        line = 1f - line
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        when (point) {
            0 -> {
                point = 1
                if (line.truthy()) context.lineTo(x, y) else context.moveTo(x, y)
            }
            else -> {
                if (point == 1) point = 2
                if (isX) {
                    x0 = (x0 + x) / 2
                    context.cubicTo(x0, y0, x0, y, x, y)
                } else {
                    y0 = (y0 + y) / 2
                    context.cubicTo(x0, y0, x, y0, x, y)
                }
            }
        }
        x0 = x
        y0 = y
    }
}

/** Bump curve with horizontal tangents at the endpoints. */
public object BumpX : Curve by Bump(isX = true)

/** Bump curve with vertical tangents at the endpoints. */
public object BumpY : Curve by Bump(isX = false)
