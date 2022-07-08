package com.juul.krayon.kanvas

import android.graphics.Path

public object AndroidPathMarker : PathTypeMarker<Path> {
    override val builder: PathBuilder<Path> get() = AndroidPathBuilder()
}
