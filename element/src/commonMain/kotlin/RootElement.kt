package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas

public class RootElement : Element() {

    override fun <PAINT, PATH> draw(canvas: Kanvas<PAINT, PATH>) {
        descendents.forEach { it.draw(canvas) }
    }

    public companion object : TypeSelector<RootElement> {
        override fun trySelect(element: Element): RootElement? = element as? RootElement
    }
}
