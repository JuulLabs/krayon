package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas

/** Breakaway [Element] allowing for raw [Kanvas] drawing. */
public class KanvasElement : Element() {

    override val tag: String get() = "kanvas"

    public var onDraw: (Kanvas.() -> Unit)? by attributes.withDefault { null }

    override fun draw(kanvas: Kanvas) {
        val callback = onDraw
        if (callback != null) {
            with(kanvas) {
                callback()
            }
        }
    }

    public companion object : ElementBuilder<KanvasElement>, ElementSelector<KanvasElement> {
        override fun build(): KanvasElement = KanvasElement()
        override fun trySelect(element: Element): KanvasElement? = element as? KanvasElement
    }
}
