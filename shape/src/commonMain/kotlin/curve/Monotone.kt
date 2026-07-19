package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN
import kotlin.math.abs

/**
 * Monotone cubic spline curve. Ported from
 * [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/monotone.js).
 */
internal class MonotoneCurve(private val reflect: Boolean) : Curve {
    private var x0: Float = NaN
    private var y0: Float = NaN
    private var x1: Float = NaN
    private var y1: Float = NaN
    private var t0: Float = NaN
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
        t0 = NaN
        point = 0
    }

    override fun endLine(context: PathBuilder<*>) {
        val ctx = if (reflect) ReflectPathBuilder(context) else context
        when (point) {
            2 -> ctx.lineTo(x1, y1)
            3 -> monotonePoint(ctx, x0, y0, x1, y1, t0, slope2(x0, y0, x1, y1, t0))
        }
        if (line.truthy() || (line != 0f && point == 1)) ctx.close()
        line = 1f - line
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        val ctx = if (reflect) ReflectPathBuilder(context) else context
        if (reflect) doPoint(ctx, y, x) else doPoint(ctx, x, y)
    }

    private fun doPoint(context: PathBuilder<*>, x: Float, y: Float) {
        var t1 = NaN
        if (x == x1 && y == y1) return

        when (point) {
            0 -> {
                point = 1
                if (line.truthy()) context.lineTo(x, y) else context.moveTo(x, y)
            }
            1 -> point = 2
            2 -> {
                point = 3
                t1 = slope3(x0, y0, x1, y1, x, y)
                monotonePoint(context, x0, y0, x1, y1, slope2(x0, y0, x1, y1, t1), t1)
            }
            else -> {
                t1 = slope3(x0, y0, x1, y1, x, y)
                monotonePoint(context, x0, y0, x1, y1, t0, t1)
            }
        }

        x0 = x1
        x1 = x
        y0 = y1
        y1 = y
        t0 = t1
    }
}

/** Monotone cubic spline where monotonicity is preserved along `x`. */
public object MonotoneX : Curve by MonotoneCurve(reflect = false)

/** Monotone cubic spline where monotonicity is preserved along `y`. */
public object MonotoneY : Curve by MonotoneCurve(reflect = true)

private fun sign(x: Float): Float = if (x < 0f) -1f else 1f

private fun slope3(x0: Float, y0: Float, x1: Float, y1: Float, x2: Float, y2: Float): Float {
    val h0 = x1 - x0
    val h1 = x2 - x1
    val d0 = if (h0 != 0f) {
        h0
    } else if (h1 < 0f) {
        -0f
    } else {
        0f
    }
    val d1 = if (h1 != 0f) {
        h1
    } else if (h0 < 0f) {
        -0f
    } else {
        0f
    }
    val s0 = (y1 - y0) / d0
    val s1 = (y2 - y1) / d1
    val p = (s0 * h1 + s1 * h0) / (h0 + h1)
    val result = (sign(s0) + sign(s1)) * minOf(abs(s0), abs(s1), 0.5f * abs(p))
    return if (result.isNaN() || result == 0f) 0f else result
}

private fun slope2(x0: Float, y0: Float, x1: Float, y1: Float, t: Float): Float {
    val h = x1 - x0
    return if (h != 0f) (3 * (y1 - y0) / h - t) / 2 else t
}

private fun monotonePoint(
    context: PathBuilder<*>,
    x0: Float,
    y0: Float,
    x1: Float,
    y1: Float,
    t0: Float,
    t1: Float,
) {
    val dx = (x1 - x0) / 3
    context.cubicTo(x0 + dx, y0 + dx * t0, x1 - dx, y1 - dx * t1, x1, y1)
}

private class ReflectPathBuilder(private val delegate: PathBuilder<*>) : PathBuilder<Unit> {
    override fun moveTo(x: Float, y: Float): Unit = delegate.moveTo(y, x)

    override fun lineTo(x: Float, y: Float): Unit = delegate.lineTo(y, x)

    override fun cubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ): Unit = delegate.cubicTo(beginControlY, beginControlX, endControlY, endControlX, endY, endX)

    override fun close(): Unit = delegate.close()

    override fun relativeMoveTo(x: Float, y: Float): Unit = unsupported()

    override fun relativeLineTo(x: Float, y: Float): Unit = unsupported()

    override fun arcTo(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        forceMoveTo: Boolean,
    ): Unit = unsupported()

    override fun quadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float): Unit = unsupported()

    override fun relativeQuadraticTo(controlX: Float, controlY: Float, endX: Float, endY: Float): Unit = unsupported()

    override fun relativeCubicTo(
        beginControlX: Float,
        beginControlY: Float,
        endControlX: Float,
        endControlY: Float,
        endX: Float,
        endY: Float,
    ): Unit = unsupported()

    override fun reset(): Unit = delegate.reset()

    override fun build() {}

    private fun unsupported(): Nothing = throw UnsupportedOperationException("Not used by monotone curves.")
}
