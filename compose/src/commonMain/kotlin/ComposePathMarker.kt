package com.juul.krayon.compose

import com.juul.krayon.kanvas.PathBuilder
import com.juul.krayon.kanvas.PathTypeMarker
import androidx.compose.ui.graphics.Path as ComposePath

public object ComposePathMarker : PathTypeMarker<ComposePath> {
    override val builder: PathBuilder<ComposePath> get() = ComposePathBuilder()
}
