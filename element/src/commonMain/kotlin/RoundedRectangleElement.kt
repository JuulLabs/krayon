package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint

public class RoundedRectangleElement : Element() {

    override val tag: String get() = "rounded-rectangle"

    public var left: Float by attributes.withDefault { 0f }
    public var top: Float by attributes.withDefault { 0f }
    public var right: Float by attributes.withDefault { 0f }
    public var bottom: Float by attributes.withDefault { 0f }

    public var topLeftRadius: Float by attributes.withDefault { 0f }
    public var topRightRadius: Float by attributes.withDefault { 0f }
    public var bottomLeftRadius: Float by attributes.withDefault { 0f }
    public var bottomRightRadius: Float by attributes.withDefault { 0f }

    public var paint: Paint by attributes.withDefault { DEFAULT_FILL }

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        val path = canvas.buildPath {
            moveTo(left + topLeftRadius, top)
            lineTo(right - topRightRadius, top)
            arcTo(right - 2 * topRightRadius, top, right, top + 2 * topRightRadius, -90f, 90f, false)
            lineTo(right, bottom - bottomRightRadius)
            arcTo(right - 2 * bottomRightRadius, bottom - 2 * bottomRightRadius, right, bottom, 0f, 90f, false)
            lineTo(left + bottomLeftRadius, bottom)
            arcTo(left, bottom - 2 * bottomLeftRadius, left + 2 * bottomLeftRadius, bottom, 90f, 90f, false)
            lineTo(left, top + topLeftRadius)
            arcTo(left, top, left + 2 * topLeftRadius, top + 2 * topLeftRadius, 180f, 90f, false)
            close()
        }
        canvas.drawPath(path, paint)
    }

    public companion object : ElementBuilder<RoundedRectangleElement>, ElementSelector<RoundedRectangleElement> {
        override fun build(): RoundedRectangleElement = RoundedRectangleElement()
        override fun trySelect(element: Element): RoundedRectangleElement? = element as? RoundedRectangleElement
    }
}
