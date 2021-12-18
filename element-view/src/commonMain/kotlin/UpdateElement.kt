package com.juul.krayon.element.view

import com.juul.krayon.element.RootElement

public fun interface UpdateElement<T> {
    public fun update(root: RootElement, width: Float, height: Float, data: T)
}
