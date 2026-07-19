package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN

/** Piecewise constant curve. Ported from [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/step.js). */
internal class StepCurve(t: Float) : Curve {
    private var t: Float = t
    private var x: Float = NaN
    private var y: Float = NaN
    private var line: Float = NaN
    private var point: Int = 0

    override fun startArea(context: PathBuilder<*>) {
        line = 0f
    }

    override fun endArea(context: PathBuilder<*>) {
        line = NaN
    }

    override fun startLine(context: PathBuilder<*>) {
        x = NaN
        y = NaN
        point = 0
    }

    override fun endLine(context: PathBuilder<*>) {
        if (t > 0f && t < 1f && point == 2) context.lineTo(x, y)
        if (line.truthy() || (line != 0f && point == 1)) context.close()
        if (line >= 0f) {
            t = 1f - t
            line = 1f - line
        }
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        when (point) {
            0 -> {
                point = 1
                if (line.truthy()) context.lineTo(x, y) else context.moveTo(x, y)
            }
            else -> {
                if (point == 1) point = 2
                if (t <= 0f) {
                    context.lineTo(this.x, y)
                    context.lineTo(x, y)
                } else {
                    val x1 = this.x * (1f - t) + x * t
                    context.lineTo(x1, this.y)
                    context.lineTo(x1, y)
                }
            }
        }
        this.x = x
        this.y = y
    }
}

/** Piecewise constant curve with the change occurring at the midpoint between adjacent points. */
public object Step : Curve by StepCurve(0.5f)

/** Piecewise constant curve with the change occurring before the current point (a vertical then horizontal step). */
public object StepBefore : Curve by StepCurve(0f)

/** Piecewise constant curve with the change occurring after the current point (a horizontal then vertical step). */
public object StepAfter : Curve by StepCurve(1f)
