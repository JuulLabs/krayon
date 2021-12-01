package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.kanvas.Kanvas

public class EnterElement : Element() {

    override val tag: String get() = "enter"

    public var next: Element? = null

    override fun <E : Element> appendChild(child: E): E =
        checkNotNull(parent).insertBefore(child, next)

    override fun <E : Element> insertBefore(child: E, reference: Element?): E =
        checkNotNull(parent).insertBefore(child, reference)

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        error("$tag should not be present in the element tree.")
    }
}
