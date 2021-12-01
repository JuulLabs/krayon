package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.withTransform

public class TransformElement : Element() {

    override val tag: String get() = "transform"

    public var transform: Transform by attributes.withDefault { Transform.Translate() }

    override fun <PAINT, PATH> draw(canvas: Kanvas<PAINT, PATH>) {
        canvas.withTransform(transform) {
            children.forEach { it.draw(canvas) }
        }
    }

    public companion object : ElementBuilder<TransformElement>, TypeSelector<TransformElement> {
        override fun build(): TransformElement = TransformElement()
        override fun trySelect(element: Element): TransformElement? = element as? TransformElement
    }
}
