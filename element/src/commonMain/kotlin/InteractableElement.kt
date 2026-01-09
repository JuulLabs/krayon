package com.juul.krayon.element

import androidx.compose.runtime.Stable
import com.juul.krayon.core.InternalKrayonApi
import com.juul.krayon.kanvas.Path

/**
 * Subclasses of this are able to receive click and hover events inside of an `ElementView`. These
 * subclasses must implement [getInteractionPath] and the individual instances must have a non-null
 * [onClick] or [onHoverChanged] handler set. These events are dispatched to the "top"-most element
 * in the tree that would be able to fire, which occludes other elements below it.
 *
 * _Only_ elements with a set handler occlude other elements (so it may sometimes be useful to set a
 * handler that no-ops to mask off another element's touch). Additionally, click and hover occlusion
 * are handled separately, so an element with a click handler but not a hover handler will not block
 * hover events below it.
 *
 * Note that the term "hover" is often different between platforms. The built-in `ElementView`s try
 * to bridge this gap, and include both _true_ hover (a mouse pointer over the element, un-pressed)
 * as well as the _press_ or _drag_ state (a mouse pointer on the element, pressed; or, a touch) as
 * hover states. This enables use of hover as a pre-selection indicator on mobile devices.
 */
@Stable
public abstract class InteractableElement<T : InteractableElement<T>> : Element() {

    @InternalKrayonApi
    public var clickHandler: ClickHandler<T>? by attributes.withDefault { null }
        private set

    @InternalKrayonApi
    public var hoverHandler: HoverHandler<T>? by attributes.withDefault { null }
        private set

    /** Set the [ClickHandler] for this element. */
    public fun onClick(handler: ClickHandler<T>?) {
        clickHandler = handler
    }

    /** Set the [HoverHandler] for this element. */
    public fun onHoverChanged(handler: HoverHandler<T>?) {
        hoverHandler = handler
    }

    /** The path used for hit detection. */
    public abstract fun getInteractionPath(): Path
}

/** Handler for click events. */
public fun interface ClickHandler<T> {
    /**
     * Called when [element] is clicked. This event is usually dispatched on _release_ of a mouse
     * press or touch.
     */
    public fun onClick(element: T)
}

/** Handler for hover events. */
public fun interface HoverHandler<T> {
    /**
     * Called when [element] starts to be hovered or stops being hovered. The [hovered] value
     * represents the _new_ state (`true` when it starts to be hovered, `false` when it stops).
     */
    public fun onHoverChanged(element: T, hovered: Boolean)
}
