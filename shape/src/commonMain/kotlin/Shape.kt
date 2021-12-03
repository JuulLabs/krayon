package com.juul.krayon.shape

import com.juul.krayon.kanvas.PathBuilder

public interface Shape<D : Any> {
    public fun render(data: List<D?>): PathBuilder<*>.() -> Unit
}
