package com.juul.krayon.compose

import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.juul.krayon.core.ExperimentalKrayonApi
import com.juul.krayon.element.Element

/**
 * Experiment API subject to change.
 *
 * Intended for use by custom, user-defined elements. This does nothing except when rendered inside of a
 * [ComposableElementView]. Inside of that view, [Content] is used instead of [draw] for rendering.
 */
@ExperimentalKrayonApi
public abstract class ComposableElement : Element() {

    @Composable
    public abstract fun BoxScope.Content(
        transformation: ImmutableMatrix,
        modifier: Modifier = Modifier,
    )
}
