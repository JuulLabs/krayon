package com.juul.krayon.element

import com.juul.krayon.kanvas.Path

/** Marker interface to enable touch/hover interaction for a type of [Element]. */
public abstract class InteractableElement<T : InteractableElement<T>> : Element() {

    public var clickHandler: ClickHandler<T>? by attributes.withDefault { null }
        private set

    public var hoverHandler: HoverHandler<T>? by attributes.withDefault { null }
        private set

    public fun onClick(handler: ClickHandler<T>?) {
        clickHandler = handler
    }

    public fun onHoverChanged(handler: HoverHandler<T>?) {
        hoverHandler = handler
    }

    public abstract fun getInteractionPath(): Path
}

public fun interface ClickHandler<T> {
    public fun onClick(element: T)
}

public fun interface HoverHandler<T> {
    /**
     * Called when [element] starts to be hovered or stops being hovered. The [hovered] value
     * represents the _new_ state (`true` when it starts to be hovered, `false` when it stops).
     */
    public fun onHoverChanged(element: T, hovered: Boolean)
}
