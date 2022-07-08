package com.juul.krayon.kanvas.svg

import com.juul.krayon.kanvas.PathBuilder
import com.juul.krayon.kanvas.PathTypeMarker
import com.juul.krayon.kanvas.xml.NumberFormatter

public data class PathStringMarker(
    val formatter: NumberFormatter,
) : PathTypeMarker<PathString> {
    override val builder: PathBuilder<PathString> get() = PathStringBuilder(formatter)
}
