package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

public class TextElement : Element() {

    override val tag: String get() = "text"

    public var text: String by attributes.withDefault { "" }
    public var x: Float by attributes.withDefault { 0f }
    public var y: Float by attributes.withDefault { 0f }
    public var paint: Paint by attributes.withDefault { DEFAULT_TEXT }

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        canvas.drawText(text, x, y, paint)
    }

    public companion object : ElementBuilder<TextElement>, ElementSelector<TextElement> {
        override fun build(): TextElement = TextElement()
        override fun trySelect(element: Element): TextElement? = element as? TextElement
    }
}
