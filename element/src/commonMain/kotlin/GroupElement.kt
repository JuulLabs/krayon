package com.juul.krayon.element

import androidx.compose.runtime.Stable
import com.juul.krayon.kanvas.Kanvas

@Stable
public class GroupElement : Element() {

    override val tag: String get() = "group"

    override fun draw(kanvas: Kanvas) {
        children.forEach { it.draw(kanvas) }
    }

    public companion object : ElementBuilder<GroupElement>, ElementSelector<GroupElement> {
        override fun build(): GroupElement = GroupElement()

        override fun trySelect(element: Element): GroupElement? = element as? GroupElement
    }
}
