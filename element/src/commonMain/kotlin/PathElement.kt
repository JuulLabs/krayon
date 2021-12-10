package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Path

public class PathElement : Element() {

    override val tag: String get() = "path"

    public var path: Path by attributes.withDefault { 0f }
    public var paint: Paint by attributes.withDefault { DEFAULT_STROKE }

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        canvas.drawPath(canvas.buildPath(path), paint)
    }

    public companion object : ElementBuilder<PathElement>, ElementSelector<PathElement> {
        override fun build(): PathElement = PathElement()
        override fun trySelect(element: Element): PathElement? = element as? PathElement
    }
}
