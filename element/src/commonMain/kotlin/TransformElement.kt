package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.withTransform

public class TransformElement(
    public var transform: Transform = Transform.Translate()
) : Element() {

    override fun <PAINT, PATH> applyTo(canvas: Kanvas<PAINT, PATH>) {
        canvas.withTransform(transform) {
            descendents.forEach { it.applyTo(canvas) }
        }
    }

    public companion object : ElementBuilder<TransformElement>, TypeSelector<TransformElement> {
        override fun build(): TransformElement = TransformElement()
        override fun trySelect(element: Element): TransformElement? = element as? TransformElement
    }
}
