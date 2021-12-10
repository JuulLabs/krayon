package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas

public class GroupElement : Element() {

    override val tag: String get() = "group"

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        children.forEach { it.draw(canvas) }
    }

    public companion object : ElementBuilder<GroupElement>, ElementSelector<GroupElement> {
        override fun build(): GroupElement = GroupElement()
        override fun trySelect(element: Element): GroupElement? = element as? GroupElement
    }
}
