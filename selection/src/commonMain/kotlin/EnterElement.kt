package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.kanvas.Kanvas

public class EnterElement: Element() {
    public var next: Element? = null

    override fun <E: Element> appendChild(child: E): E =
        checkNotNull(parent).insertBefore(child, next)

    override fun <E: Element> insertBefore(child: E, reference: Element?): E =
        checkNotNull(parent).insertBefore(child, reference)

    override fun <PAINT, PATH> applyTo(canvas: Kanvas<PAINT, PATH>) {
        error("EnterElement should not be present in the DOM.")
    }
}
