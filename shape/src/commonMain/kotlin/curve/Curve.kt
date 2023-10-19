package com.juul.krayon.shape.curve

import com.juul.krayon.kanvas.PathBuilder

public interface Curve {
    public fun startArea(context: PathBuilder<*>)

    public fun endArea(context: PathBuilder<*>)

    public fun startLine(context: PathBuilder<*>)

    public fun endLine(context: PathBuilder<*>)

    public fun point(context: PathBuilder<*>, x: Float, y: Float)
}
