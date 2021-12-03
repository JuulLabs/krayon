package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Path

public class PathElement : Element() {

    override val tag: String get() = "path"

    public var path: Path by attributes.withDefault { 0f }
    public var paint: Paint by attributes.withDefault { DEFAULT_PAINT }

    override fun <PAINT, PATH> draw(canvas: Kanvas<PAINT, PATH>) {
        canvas.drawPath(canvas.buildPath(path), canvas.buildPaint(paint))
    }

    public companion object : ElementBuilder<PathElement>, TypeSelector<PathElement> {
        override fun build(): PathElement = PathElement()
        override fun trySelect(element: Element): PathElement? = element as? PathElement
    }
}
