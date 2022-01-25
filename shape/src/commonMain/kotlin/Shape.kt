package com.juul.krayon.shape

import com.juul.krayon.kanvas.Path

@Deprecated("Interface doesn't really add much/not every shape renders from the same data.")
public interface Shape<D : Any> {
    public fun render(data: List<D?>): Path
}
