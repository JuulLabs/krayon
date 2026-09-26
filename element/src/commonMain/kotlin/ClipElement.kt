package com.juul.krayon.element

import androidx.compose.runtime.Stable
import com.juul.krayon.kanvas.Clip
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.withClip

/**
 * An element that clips rendering of child elements.
 *
 * Note that this does not prevent clipped children from being hit-tested for interactions.
 */
@Stable
public class ClipElement : Element() {

    override val tag: String get() = "clip"

    public var clip: Clip? by attributes.withDefault { null }

    override fun draw(kanvas: Kanvas) {
        when (val clip = this.clip) {
            null -> children.forEach { it.draw(kanvas) }
            else -> kanvas.withClip(clip) {
                children.forEach { it.draw(kanvas) }
            }
        }
    }

    public companion object : ElementBuilder<ClipElement>, ElementSelector<ClipElement> {
        override fun build(): ClipElement = ClipElement()

        override fun trySelect(element: Element): ClipElement? = element as? ClipElement
    }
}
