package com.juul.krayon.kanvas

import org.w3c.dom.Path2D

public object Path2DMarker : PathTypeMarker<Path2D> {
    override val builder: PathBuilder<Path2D> get() = Path2DBuilder()
}
