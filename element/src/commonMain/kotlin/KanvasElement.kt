package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas

public class KanvasElement : Element() {

    override val tag: String get() = "kanvas"

    public var onDraw: (Kanvas<*>.() -> Unit)? by attributes.withDefault { null }

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        onDraw?.invoke(canvas)
    }

    public companion object : ElementBuilder<KanvasElement>, ElementSelector<KanvasElement> {
        override fun build(): KanvasElement = KanvasElement()
        override fun trySelect(element: Element): KanvasElement? = element as? KanvasElement
    }
}
