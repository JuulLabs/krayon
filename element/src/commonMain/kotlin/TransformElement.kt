package com.juul.krayon.element

import androidx.compose.runtime.Stable
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.withTransform

@Stable
public class TransformElement : Element() {

    override val tag: String get() = "transform"

    public var transform: Transform by attributes.withDefault { Transform.Translate() }

    override fun draw(kanvas: Kanvas) {
        kanvas.withTransform(transform) {
            children.forEach { it.draw(kanvas) }
        }
    }

    public companion object : ElementBuilder<TransformElement>, ElementSelector<TransformElement> {
        override fun build(): TransformElement = TransformElement()

        override fun trySelect(element: Element): TransformElement? = element as? TransformElement
    }
}
