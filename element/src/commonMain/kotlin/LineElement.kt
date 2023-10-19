package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

public class LineElement : Element() {

    override val tag: String get() = "line"

    public var startX: Float by attributes.withDefault { 0f }
    public var startY: Float by attributes.withDefault { 0f }
    public var endX: Float by attributes.withDefault { 0f }
    public var endY: Float by attributes.withDefault { 0f }
    public var paint: Paint by attributes.withDefault { DEFAULT_STROKE }

    override fun draw(kanvas: Kanvas) {
        kanvas.drawLine(startX, startY, endX, endY, paint)
    }

    public companion object : ElementBuilder<LineElement>, ElementSelector<LineElement> {
        override fun build(): LineElement = LineElement()

        override fun trySelect(element: Element): LineElement? = element as? LineElement
    }
}
