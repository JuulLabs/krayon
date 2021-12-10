package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas

public class RootElement : Element() {

    override val tag: String get() = "root"

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        children.forEach { it.draw(canvas) }
    }

    public companion object : ElementSelector<RootElement> {
        override fun trySelect(element: Element): RootElement? = element as? RootElement
    }
}
