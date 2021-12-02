package com.juul.krayon.shape

import com.juul.krayon.kanvas.Path

public interface Shape<D : Any> {
    public fun render(data: List<D?>): Path
}
