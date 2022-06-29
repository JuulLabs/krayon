package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.circlePath

public class CircleElement : Element(), Interactable<CircleElement> {

    override val tag: String get() = "circle"

    public var centerX: Float by attributes.withDefault { 0f }
    public var centerY: Float by attributes.withDefault { 0f }
    public var radius: Float by attributes.withDefault { 0f }
    public var paint: Paint by attributes.withDefault { DEFAULT_FILL }
    override var onClick: ((CircleElement) -> Unit)? by attributes.withDefault { null }

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        canvas.drawCircle(centerX, centerY, radius, paint)
    }

    override fun getInteractionPath(): Path = circlePath(centerX, centerY, radius)

    public companion object : ElementBuilder<CircleElement>, ElementSelector<CircleElement> {
        override fun build(): CircleElement = CircleElement()
        override fun trySelect(element: Element): CircleElement? = element as? CircleElement
    }
}
