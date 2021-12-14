package com.juul.krayon.element

public interface ElementSelector<E : Element> {
    public fun trySelect(element: Element): E?
}
