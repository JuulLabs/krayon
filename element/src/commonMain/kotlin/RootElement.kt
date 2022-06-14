package com.juul.krayon.element

import com.juul.krayon.kanvas.IsPointInPath
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.Transform.Translate

public class RootElement : Element() {

    override val tag: String get() = "root"

    public var onClickFallback: (() -> Unit)? by attributes.withDefault { null }

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        children.forEach { it.draw(canvas) }
    }

    /** Returns true if an element was found and clicked on. Returns false if no element matched. */
    public fun onClick(isPointInPath: IsPointInPath, x: Float, y: Float): Boolean {
        val interactable = visibilityOrderedDescendants()
            .filterIsInstance<Interactable<*>>()
            .filter { it.onClick != null }
            .firstOrNull { interactable ->
                val transform = (interactable as Element).totalTransform()
                isPointInPath.isPointInPath(transform, interactable.getInteractionPath(), x, y)
            }
        val fallback = onClickFallback
        return when {
            interactable != null -> {
                @Suppress("UNCHECKED_CAST") // Interactable<T> always accepts itself as the type argument.
                val onClick = interactable.onClick as (Element) -> Unit
                onClick(interactable as Element)
                true
            }
            fallback != null -> {
                fallback()
                true
            }
            else -> {
                false
            }
        }
    }

    public companion object : ElementSelector<RootElement> {
        override fun trySelect(element: Element): RootElement? = element as? RootElement
    }
}

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

private fun Element.visibilityOrderedDescendants(): Sequence<Element> = sequence {
    for (child in children.asReversed()) {
        yieldAll(child.visibilityOrderedDescendants())
    }
    yield(this@visibilityOrderedDescendants)
}
