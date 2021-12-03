package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

public class RectangleElement : Element() {

    override val tag: String get() = "rectangle"

    public var left: Float by attributes.withDefault { 0f }
    public var top: Float by attributes.withDefault { 0f }
    public var right: Float by attributes.withDefault { 0f }
    public var bottom: Float by attributes.withDefault { 0f }
    public var paint: Paint by attributes.withDefault { DEFAULT_PAINT }

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        canvas.drawRect(left, top, right, bottom, paint)
    }

    public companion object : ElementBuilder<RectangleElement>, TypeSelector<RectangleElement> {
        override fun build(): RectangleElement = RectangleElement()
        override fun trySelect(element: Element): RectangleElement? = element as? RectangleElement
    }
}
