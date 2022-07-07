package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.ElementSelector
import com.juul.krayon.kanvas.Kanvas

public class EnterElement : Element() {

    override val tag: String get() = "enter"

    public var next: Element? = null

    override fun <E : Element> appendChild(child: E): E =
        checkNotNull(parent).insertBefore(child, next)

    override fun <E : Element> insertBefore(child: E, reference: Element?): E =
        checkNotNull(parent).insertBefore(child, reference)

    override fun <E : Element> query(selector: ElementSelector<E>): E? =
        checkNotNull(parent).query(selector)

    override fun <E : Element> queryAll(selector: ElementSelector<E>): Sequence<E> =
        checkNotNull(parent).queryAll(selector)

    override fun draw(kanvas: Kanvas) {
        error("$tag should not be present in the element tree.")
    }
}
