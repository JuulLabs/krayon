package com.juul.krayon.element

public interface TypeSelector<E : Element> {
    public fun trySelect(element: Element): E?
}
