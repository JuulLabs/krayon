package com.juul.krayon.element

import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.reify

public class RoundedRectangleElement : Element(), Interactable<RoundedRectangleElement> {

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

    override var onClick: ((RoundedRectangleElement) -> Unit)? by attributes.withDefault { null }

    // TODO: Cache the generated path, lazily generating it only when it changes.

    override fun <PATH> draw(canvas: Kanvas<PATH>) {
        canvas.drawPath(canvas.buildPath(generatePath()), paint)
    }

    override fun getInteractionPath(): Path = generatePath()

    public companion object : ElementBuilder<RoundedRectangleElement>, ElementSelector<RoundedRectangleElement> {
        override fun build(): RoundedRectangleElement = RoundedRectangleElement()
        override fun trySelect(element: Element): RoundedRectangleElement? = element as? RoundedRectangleElement
    }
}

/** Transforms a radius such that requested radii which are larger than their rectangle behave nicely. */
private fun safeRadius(desired: Float, horizontalNeighbor: Float, verticalNeighbor: Float, width: Float, height: Float): Float {
    val requiresHorizontalAdjustment = width < (desired + horizontalNeighbor)
    val requiresVerticalAdjustment = height < (desired + verticalNeighbor)
    return if (requiresHorizontalAdjustment && requiresVerticalAdjustment) {
        minOf(
            width * desired / (desired + horizontalNeighbor),
            height * desired / (desired + verticalNeighbor),
        )
    } else if (requiresHorizontalAdjustment) {
        width * desired / (desired + horizontalNeighbor)
    } else if (requiresVerticalAdjustment) {
        height * desired / (desired + verticalNeighbor)
    } else {
        desired
    }
}

private fun RoundedRectangleElement.generatePath(): Path {
    val width = (right - left).coerceAtLeast(0f)
    val height = (bottom - top).coerceAtLeast(0f)
    val tlr = safeRadius(topLeftRadius, horizontalNeighbor = topRightRadius, verticalNeighbor = bottomLeftRadius, width, height)
    val trr = safeRadius(topRightRadius, horizontalNeighbor = topLeftRadius, verticalNeighbor = bottomRightRadius, width, height)
    val blr = safeRadius(bottomLeftRadius, horizontalNeighbor = bottomRightRadius, verticalNeighbor = topLeftRadius, width, height)
    val brr = safeRadius(bottomRightRadius, horizontalNeighbor = bottomLeftRadius, verticalNeighbor = topRightRadius, width, height)
    return Path {
        moveTo(left + tlr, top)
        lineTo(right - trr, top)
        arcTo(right - 2 * trr, top, right, top + 2 * trr, -90f, 90f, false)
        lineTo(right, bottom - brr)
        arcTo(right - 2 * brr, bottom - 2 * brr, right, bottom, 0f, 90f, false)
        lineTo(left + blr, bottom)
        arcTo(left, bottom - 2 * blr, left + 2 * blr, bottom, 90f, 90f, false)
        lineTo(left, top + tlr)
        arcTo(left, top, left + 2 * tlr, top + 2 * tlr, 180f, 90f, false)
        close()
    }.reify()
}
