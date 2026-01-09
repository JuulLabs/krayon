package com.juul.krayon.element

import androidx.compose.runtime.Stable
import com.juul.krayon.element.InteractableType.Click
import com.juul.krayon.element.InteractableType.Hover
import com.juul.krayon.kanvas.IsPointInPath
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.Transform.Translate

@Stable
public class RootElement : Element() {

    override val tag: String get() = "root"

    /**
     * The currently hovered value, recorded as an implementation detail of the hover-off event.
     *
     * This is NOT implemented as `by attribute` to avoid leaking its visibility.
     */
    private var hoveredElement: InteractableElement<*>? = null

    /**
     * If set, this callback is invoked when [onClick] is called but no descendant element handles
     * that event.
     *
     * An example of when this is useful would be implementing a deselection behavior, where clicking
     * on an element selects that element and clicking anywhere else on the chart deselects it.
     */
    public var onClickFallback: (() -> Unit)? by attributes.withDefault { null }

    override fun draw(kanvas: Kanvas) {
        children.forEach { it.draw(kanvas) }
    }

    /**
     * Entry point for dispatching hover events, both start and move. Usually you won't need to call
     * this, and it will be handled by your platform-specific ElementView.
     */
    public fun onHover(isPointInPath: IsPointInPath, x: Float, y: Float) {
        val previousHoveredElement = hoveredElement
        val newHoveredElement = interactableAtPoint(isPointInPath, x, y, type = Hover)
        if (newHoveredElement != previousHoveredElement) {
            if (previousHoveredElement != null) {
                @Suppress("UNCHECKED_CAST") // Interactables always accepts themselves as the type argument.
                val handler = previousHoveredElement.hoverHandler as HoverHandler<Element>?
                handler?.onHoverChanged(previousHoveredElement, hovered = false)
            }
            if (newHoveredElement != null) {
                @Suppress("UNCHECKED_CAST") // Interactables always accepts themselves as the type argument.
                val handler = newHoveredElement.hoverHandler as HoverHandler<Element>
                handler.onHoverChanged(newHoveredElement, hovered = true)
            }
            hoveredElement = newHoveredElement
        }
    }

    /**
     * Entry point for dispatching hover-end events. Usually you won't need to call this, and it will be
     * handled by your platform-specific ElementView.
     */
    public fun onHoverEnded() {
        val element = hoveredElement ?: return

        @Suppress("UNCHECKED_CAST") // Interactables always accepts themselves as the type argument.
        val handler = element.hoverHandler as HoverHandler<Element>? ?: return
        handler.onHoverChanged(element, hovered = false)
        hoveredElement = null
    }

    /**
     * Entry point for dispatching click events. Usually you won't need to call this, and it will be
     * handled by your platform-specific ElementView.
     */
    public fun onClick(isPointInPath: IsPointInPath, x: Float, y: Float) {
        val clickedElement = interactableAtPoint(isPointInPath, x, y, type = Click)
        val fallback = onClickFallback
        if (clickedElement != null) {
            @Suppress("UNCHECKED_CAST") // Interactables always accepts themselves as the type argument.
            val handler = clickedElement.clickHandler as ClickHandler<Element>
            handler.onClick(clickedElement)
        } else if (fallback != null) {
            fallback()
        }
    }

    public companion object : ElementSelector<RootElement> {
        override fun trySelect(element: Element): RootElement? = element as? RootElement
    }
}

/** Type-safe argument for [interactableAtPoint]. */
private enum class InteractableType { Click, Hover }

/**
 * Returns all descendants of this [Element], sorted by visibility. This means that later elements
 * always come before earlier elements, and children always come before their parent.
 */
private fun Element.visibilityOrderedDescendants(): Sequence<Element> = sequence {
    for (child in children.asReversed()) {
        yieldAll(child.visibilityOrderedDescendants())
    }
    yield(this@visibilityOrderedDescendants)
}

/** Returns the total transform that will affect how this element draws. */
private fun Element.totalTransform(): Transform {
    var element: Element? = this
    var transform: Transform = Translate() // Start with a no-op/identity transform
    while (element != null) {
        if (element is TransformElement) {
            transform = Transform.InOrder(element.transform, transform)
        }
        element = element.parent
    }
    return transform
}

/**
 * Finds the element that consumes/receives interaction. Depending on [type], this will only include
 * elements that have the appropriate handler installed.
 */
private fun Element.interactableAtPoint(
    isPointInPath: IsPointInPath,
    x: Float,
    y: Float,
    type: InteractableType,
): InteractableElement<*>? = visibilityOrderedDescendants()
    .filterIsInstance<InteractableElement<*>>()
    .filter { element ->
        when (type) {
            Click -> element.clickHandler != null
            Hover -> element.hoverHandler != null
        }
    }.firstOrNull { interactable ->
        val transform = (interactable as Element).totalTransform()
        isPointInPath.isPointInPath(transform, interactable.getInteractionPath(), x, y)
    }
