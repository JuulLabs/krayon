package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder

internal const val EPSILON: Float = 1e-12f

/** Emulate JS truthiness for `NaN`/`0` sentinels used by the D3 curve algorithms. */
internal fun Float.truthy(): Boolean = !isNaN() && this != 0f

internal fun basisPoint(
    context: PathBuilder<*>,
    x0: Float,
    y0: Float,
    x1: Float,
    y1: Float,
    x: Float,
    y: Float,
) {
    context.cubicTo(
        (2 * x0 + x1) / 3,
        (2 * y0 + y1) / 3,
        (x0 + 2 * x1) / 3,
        (y0 + 2 * y1) / 3,
        (x0 + 4 * x1 + x) / 6,
        (y0 + 4 * y1 + y) / 6,
    )
}

internal fun cardinalPoint(
    context: PathBuilder<*>,
    k: Float,
    x0: Float,
    y0: Float,
    x1: Float,
    y1: Float,
    x2: Float,
    y2: Float,
    x: Float,
    y: Float,
) {
    context.cubicTo(
        x1 + k * (x2 - x0),
        y1 + k * (y2 - y0),
        x2 + k * (x1 - x),
        y2 + k * (y1 - y),
        x2,
        y2,
    )
}
