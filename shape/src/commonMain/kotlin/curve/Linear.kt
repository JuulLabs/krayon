package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder
import kotlin.Float.Companion.NaN

public object Linear : Curve {
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
        if (line.truthy() || (line != 0f && point == 1)) {
            context.close()
        }
        line = 1f - line
    }

    override fun point(context: PathBuilder<*>, x: Float, y: Float) {
        when (point) {
            0 -> {
                point = 1
                if (line.truthy()) {
                    context.lineTo(x, y)
                } else {
                    context.moveTo(x, y)
                }
            }
            1 -> {
                point = 2
                context.lineTo(x, y)
            }
            else -> {
                context.lineTo(x, y)
            }
        }
    }
}

/** Emulate JS truthiness. */
private fun Float.truthy(): Boolean = (!this.isNaN() && this != 0f)
