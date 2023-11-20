package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Path

public class RectangleElement : InteractableElement<RectangleElement>() {

    override val tag: String get() = "rectangle"

    public var left: Float by attributes.withDefault { 0f }
    public var top: Float by attributes.withDefault { 0f }
    public var right: Float by attributes.withDefault { 0f }
    public var bottom: Float by attributes.withDefault { 0f }
    public var paint: Paint by attributes.withDefault { DEFAULT_FILL }

    override fun draw(kanvas: Kanvas) {
        kanvas.drawRect(left, top, right, bottom, paint)
    }

    override fun getInteractionPath(): Path = Path {
        moveTo(left, top)
        lineTo(right, top)
        lineTo(right, bottom)
        lineTo(left, bottom)
        close()
    }

    public companion object : ElementBuilder<RectangleElement>, ElementSelector<RectangleElement> {
        override fun build(): RectangleElement = RectangleElement()

        override fun trySelect(element: Element): RectangleElement? = element as? RectangleElement
    }
}
