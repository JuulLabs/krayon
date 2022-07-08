package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Path

public class PathElement : Element(), Interactable<PathElement> {

    override val tag: String get() = "path"

    public var path: Path by attributes.withDefault { 0f }
    public var paint: Paint by attributes.withDefault { DEFAULT_STROKE }
    override var onClick: ((PathElement) -> Unit)? by attributes.withDefault { null }

    override fun draw(kanvas: Kanvas) {
        kanvas.drawPath(path, paint)
    }

    override fun getInteractionPath(): Path = path

    public companion object : ElementBuilder<PathElement>, ElementSelector<PathElement> {
        override fun build(): PathElement = PathElement()
        override fun trySelect(element: Element): PathElement? = element as? PathElement
    }
}
