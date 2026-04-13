package com.juul.krayon.shape

import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.PathBuilder
import com.juul.krayon.shape.curve.Curve
import com.juul.krayon.shape.curve.Linear

public fun <D : Any> area(): Area<D> = Area()

public class Area<D : Any> internal constructor() {

    private var curve: Curve = Linear
    private var defined: (Arguments<D>) -> Boolean = { true }
    private var x0: (Arguments<D>) -> Float = pointX
    private var x1: ((Arguments<D>) -> Float)? = null
    private var y0: (Arguments<D>) -> Float = { 0f }
    private var y1: ((Arguments<D>) -> Float)? = pointY

    public fun curve(curve: Curve): Area<D> = apply { this.curve = curve }

    public fun defined(defined: (Arguments<D>) -> Boolean): Area<D> = apply { this.defined = defined }

    public fun x(x: Float): Area<D> = apply {
        this.x0 = { x }
        this.x1 = null
    }

    public fun x(x: (Arguments<D>) -> Float): Area<D> = apply {
        this.x0 = x
        this.x1 = null
    }

    public fun x0(x0: Float): Area<D> = apply { this.x0 = { x0 } }

    public fun x0(x0: (Arguments<D>) -> Float): Area<D> = apply { this.x0 = x0 }

    public fun x1(x1: Float): Area<D> = apply { this.x1 = { x1 } }

    public fun x1(x1: (Arguments<D>) -> Float): Area<D> = apply { this.x1 = x1 }

    public fun y(y: Float): Area<D> = apply {
        this.y0 = { y }
        this.y1 = null
    }

    public fun y(y: (Arguments<D>) -> Float): Area<D> = apply {
        this.y0 = y
        this.y1 = null
    }

    public fun y0(y0: Float): Area<D> = apply { this.y0 = { y0 } }

    public fun y0(y0: (Arguments<D>) -> Float): Area<D> = apply { this.y0 = y0 }

    public fun y1(y1: Float): Area<D> = apply { this.y1 = { y1 } }

    public fun y1(y1: (Arguments<D>) -> Float): Area<D> = apply { this.y1 = y1 }

    public fun render(data: List<D?>): Path = Path {
        var currentlyDefined = false
        var segmentStart = 0
        val x0z = FloatArray(data.size)
        val y0z = FloatArray(data.size)
        val arguments = Arguments(Unit, -1, data)

        for ((index, datum) in data.withIndex()) {
            val isDefined = datum != null && run {
                arguments.datum = datum
                arguments.index = index
                @Suppress("UNCHECKED_CAST")
                defined(arguments as Arguments<D>)
            }

            if (isDefined != currentlyDefined) {
                currentlyDefined = isDefined
                if (isDefined) {
                    segmentStart = index
                    curve.startArea(this)
                    curve.startLine(this)
                } else {
                    endSegment(segmentStart, index - 1, x0z, y0z)
                }
            }

            if (currentlyDefined) {
                @Suppress("UNCHECKED_CAST")
                arguments as Arguments<D>
                x0z[index] = x0(arguments)
                y0z[index] = y0(arguments)
                curve.point(this, x1?.invoke(arguments) ?: x0z[index], y1?.invoke(arguments) ?: y0z[index])
            }
        }

        if (currentlyDefined) {
            endSegment(segmentStart, data.size - 1, x0z, y0z)
        }
    }

    private fun <T> PathBuilder<T>.endSegment(
        start: Int,
        end: Int,
        x0z: FloatArray,
        y0z: FloatArray,
    ) {
        curve.endLine(this)
        curve.startLine(this)
        for (k in end downTo start) {
            curve.point(this, x0z[k], y0z[k])
        }
        curve.endLine(this)
        curve.endArea(this)
    }
}
