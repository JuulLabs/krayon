package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder

/** Closed polyline curve. Ported from [d3-shape](https://github.com/d3/d3-shape/blob/main/src/curve/linearClosed.js). */
public object LinearClosed : Curve {
    private var point: Int = 0

    override fun startArea(context: PathBuilder<*>) {}

    override fun endArea(context: PathBuilder<*>) {}

    override fun startLine(context: PathBuilder<*>) {
        point = 0
    }

    override fun endLine(context: PathBuilder<*>) {
        if (point != 0) context.close()
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        if (point != 0) {
            context.lineTo(x, y)
        } else {
            point = 1
            context.moveTo(x, y)
        }
    }
}
