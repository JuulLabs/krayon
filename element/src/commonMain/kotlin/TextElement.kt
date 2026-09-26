package com.juul.krayon.element

import androidx.compose.runtime.Stable
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

@Stable
public class TextElement : Element() {

    override val tag: String get() = "text"

    public var text: String by attributes.withDefault { "" }
    public var x: Float by attributes.withDefault { 0f }
    public var y: Float by attributes.withDefault { 0f }
    public var paint: Paint.Text by attributes.withDefault { DEFAULT_TEXT }

    /** Vertical alignment, as a ratio of [Paint.Text.size]. */
    public var verticalAlign: Float by attributes.withDefault { 0f }

    override fun draw(kanvas: Kanvas) {
        kanvas.drawText(text, x, y + (paint.size * verticalAlign), paint)
    }

    public companion object : ElementBuilder<TextElement>, ElementSelector<TextElement> {
        override fun build(): TextElement = TextElement()

        override fun trySelect(element: Element): TextElement? = element as? TextElement
    }
}
