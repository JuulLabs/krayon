package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN

/** Natural cubic spline curve. Ported from [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/natural.js). */
public object Natural : Curve {
    private val xs: MutableList<Float> = mutableListOf()
    private val ys: MutableList<Float> = mutableListOf()
    private var line: Float = NaN

    override fun startArea(context: PathBuilder<*>) {
        line = 0f
    }

    override fun endArea(context: PathBuilder<*>) {
        line = NaN
    }

    override fun startLine(context: PathBuilder<*>) {
        xs.clear()
        ys.clear()
    }

    override fun endLine(context: PathBuilder<*>) {
        val n = xs.size
        if (n > 0) {
            if (line.truthy()) context.lineTo(xs[0], ys[0]) else context.moveTo(xs[0], ys[0])
            if (n == 2) {
                context.lineTo(xs[1], ys[1])
            } else if (n > 2) {
                val px = controlPoints(xs)
                val py = controlPoints(ys)
                var i0 = 0
                var i1 = 1
                while (i1 < n) {
                    context.cubicTo(px[0][i0], py[0][i0], px[1][i0], py[1][i0], xs[i1], ys[i1])
                    i0++
                    i1++
                }
            }
        }
        if (line.truthy() || (line != 0f && n == 1)) context.close()
        line = 1f - line
        xs.clear()
        ys.clear()
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        xs += x
        ys += y
    }
}

private fun controlPoints(x: List<Float>): Array<FloatArray> {
    val n = x.size - 1
    val a = FloatArray(n)
    val b = FloatArray(n)
    val r = FloatArray(n)
    a[0] = 0f
    b[0] = 2f
    r[0] = x[0] + 2 * x[1]
    for (i in 1 until n - 1) {
        a[i] = 1f
        b[i] = 4f
        r[i] = 4 * x[i] + 2 * x[i + 1]
    }
    a[n - 1] = 2f
    b[n - 1] = 7f
    r[n - 1] = 8 * x[n - 1] + x[n]
    for (i in 1 until n) {
        val m = a[i] / b[i - 1]
        b[i] -= m
        r[i] -= m * r[i - 1]
    }
    a[n - 1] = r[n - 1] / b[n - 1]
    for (i in n - 2 downTo 0) {
        a[i] = (r[i] - a[i + 1]) / b[i]
    }
    b[n - 1] = (x[n] + a[n - 1]) / 2
    for (i in 0 until n - 1) {
        b[i] = 2 * x[i + 1] - a[i + 1]
    }
    return arrayOf(a, b)
}
