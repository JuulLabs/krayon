package com.juul.krayon.element

public fun interface UpdateElement<T> {
    public fun update(root: RootElement, width: Float, height: Float, data: T)
}
