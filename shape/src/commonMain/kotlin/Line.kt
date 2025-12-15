package com.juul.krayon.shape

import com.juul.krayon.kanvas.Path
import com.juul.krayon.shape.curve.Curve
import com.juul.krayon.shape.curve.Linear

private val DEFAULT_DEFINED = { _: Arguments<*> -> true }
private val DEFAULT_XY = { (datum): Arguments<*> -> (datum as? Number)?.toFloat() ?: 0f }

public fun <D : Any> line(): Line<D> = Line()

public class Line<D : Any> internal constructor() {

    private var curve: Curve = Linear
    private var defined: (Arguments<D>) -> Boolean = DEFAULT_DEFINED
    private var x: (Arguments<D>) -> Float = DEFAULT_XY
    private var y: (Arguments<D>) -> Float = DEFAULT_XY

    public fun curve(curve: Curve): Line<D> = this.apply { this.curve = curve }

    public fun defined(defined: (Arguments<D>) -> Boolean): Line<D> = this.apply { this.defined = defined }

    public fun x(x: Float): Line<D> = this.apply { this.x = { x } }

    public fun x(x: (Arguments<D>) -> Float): Line<D> = this.apply { this.x = x }

    public fun y(y: Float): Line<D> = this.apply { this.y = { y } }

    public fun y(y: (Arguments<D>) -> Float): Line<D> = this.apply { this.y = y }

    public fun render(data: List<D?>): Path = Path {
        var currentlyDefined = false
        val arguments = Arguments(Unit, -1, data)
        for ((index, datum) in data.withIndex()) {
            if (datum == null) {
                if (currentlyDefined) {
                    curve.endLine(this)
                    currentlyDefined = false
                }
                continue
            }

            arguments.datum = datum
            arguments.index = index
            @Suppress("UNCHECKED_CAST")
            arguments as Arguments<D>
            if (defined(arguments) != currentlyDefined) {
                if (currentlyDefined) {
                    curve.endLine(this)
                } else {
                    curve.startLine(this)
                }
                currentlyDefined = !currentlyDefined
            }
            if (currentlyDefined) {
                curve.point(this, x(arguments), y(arguments))
            }
        }
    }
}
