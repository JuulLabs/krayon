package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

public class CircleElement : Element() {

    override val tag: String get() = "circle"

    public var centerX: Float by attributes.withDefault { 0f }
    public var centerY: Float by attributes.withDefault { 0f }
    public var radius: Float by attributes.withDefault { 0f }
    public var paint: Paint by attributes.withDefault { DEFAULT_PAINT }

    override fun <PAINT, PATH> draw(canvas: Kanvas<PAINT, PATH>) {
        canvas.drawCircle(centerX, centerY, radius, canvas.buildPaint(paint))
    }

    public companion object : ElementBuilder<CircleElement>, TypeSelector<CircleElement> {
        override fun build(): CircleElement = CircleElement()
        override fun trySelect(element: Element): CircleElement? = element as? CircleElement
    }
}
