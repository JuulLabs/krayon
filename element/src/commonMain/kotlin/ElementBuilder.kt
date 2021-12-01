package com.juul.krayon.element

public interface ElementBuilder<E : Element> {
    public fun build(): E
}
