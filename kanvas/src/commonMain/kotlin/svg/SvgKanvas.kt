package com.juul.krayon.kanvas.svg

import com.juul.krayon.color.Color
import com.juul.krayon.kanvas.Clip
import com.juul.krayon.kanvas.Kanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Path
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.split
import com.juul.krayon.kanvas.xml.NumberFormatter
import com.juul.krayon.kanvas.xml.ToStringFormatter
import com.juul.krayon.kanvas.xml.XmlElement
import com.juul.krayon.kanvas.xml.escape
import kotlin.math.roundToInt

public class SvgKanvas(
    override val width: Float,
    override val height: Float,
    private val formatter: NumberFormatter = ToStringFormatter,
) : Kanvas {

    private val marker = PathStringMarker(formatter)

    /** Root XML element. */
    private val root = XmlElement("svg")
        .setAttribute("xmlns", "http://www.w3.org/2000/svg")
        .setAttribute("viewBox", "0 0 ${formatter(width)} ${formatter(height)}")

    /** Current path to the root element, with root and index [0] and the current group as the last. */
    private val xmlAncestors = ArrayDeque<XmlElement>().apply { addLast(root) }

    /** ID to use for the next clip path. */
    private var clipCount = 0

    /** ID to use for the next gradient. */
    private var gradientCount = 0

    override fun drawArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        paint: Paint,
    ) {
        drawPath(Path { arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo = true) }, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint) {
        val element = XmlElement("circle")
            .setAttribute("cx", centerX, formatter)
            .setAttribute("cy", centerY, formatter)
            .setAttribute("r", radius, formatter)
            .setPaintAttributes(paint, formatter, injectGradientDef(paint))
        xmlAncestors.last().addContent(element)
    }

    override fun drawColor(color: Color) {
        val element = XmlElement("rect")
            .setAttribute("width", "100%")
            .setAttribute("height", "100%")
            .setPaintAttributes(Paint.Fill(color), formatter, null)
        xmlAncestors.last().addContent(element)
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint) {
        val element = XmlElement("line")
            .setAttribute("x1", startX, formatter)
            .setAttribute("y1", startY, formatter)
            .setAttribute("x2", endX, formatter)
            .setAttribute("y2", endY, formatter)
            .setPaintAttributes(paint, formatter, null)
            .unsetAttribute("fill") // lines have no area to fill
        xmlAncestors.last().addContent(element)
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        val rx = (right - left) / 2.0
        val ry = (bottom - top) / 2.0
        val cx = left + rx
        val cy = top + ry
        val element = XmlElement("ellipse")
            .setAttribute("cx", cx, formatter)
            .setAttribute("cy", cy, formatter)
            .setAttribute("rx", rx, formatter)
            .setAttribute("ry", ry, formatter)
            .setPaintAttributes(paint, formatter, injectGradientDef(paint))
        xmlAncestors.last().addContent(element)
    }

    override fun drawPath(path: Path, paint: Paint) {
        val element = XmlElement("path")
            .setAttribute("d", path.get(marker).string)
            .setPaintAttributes(paint, formatter, injectGradientDef(paint))
        xmlAncestors.last().addContent(element)
    }

    override fun drawRect(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        val element = XmlElement("rect")
            .setAttribute("x", left, formatter)
            .setAttribute("y", top, formatter)
            .setAttribute("width", right - left, formatter)
            .setAttribute("height", bottom - top, formatter)
            .setPaintAttributes(paint, formatter, injectGradientDef(paint))
        xmlAncestors.last().addContent(element)
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint) {
        val element = XmlElement("text")
            .setAttribute("x", x, formatter)
            .setAttribute("y", y, formatter)
            .setPaintAttributes(paint, formatter, null)
            .addContent(text.toString().escape())
        xmlAncestors.last().addContent(element)
    }

    override fun pushClip(clip: Clip) {
        // TODO: As an optimization, it should be possible to use a single `defs` object for the whole vector.
        val pathName = "c$clipCount"
        clipCount += 1
        val clipPathElement = when (clip) {
            is Clip.Rect ->
                XmlElement("rect")
                    .setAttribute("x", clip.left, formatter)
                    .setAttribute("y", clip.top, formatter)
                    .setAttribute("width", clip.right - clip.left, formatter)
                    .setAttribute("height", clip.bottom - clip.top, formatter)
            is Clip.Path ->
                XmlElement("path")
                    .setAttribute("d", clip.path.get(marker).string)
        }
        val clipPath = XmlElement("clipPath")
            .setAttribute("id", pathName)
            .addContent(clipPathElement)
        val defs = XmlElement("defs")
            .addContent(clipPath)
        xmlAncestors.last().addContent(defs)
        val group = XmlElement("g")
            .setAttribute("clip-path", "url(#$pathName)")
        xmlAncestors.last().addContent(group)
        xmlAncestors.addLast(group)
    }

    override fun pushTransform(transform: Transform) {
        // Recursively create a transformation string.
        // See https://developer.mozilla.org/en-US/docs/Web/SVG/Attribute/transform
        fun stringify(t: Transform): String = when (t) {
            is Transform.InOrder ->
                t.transformations.joinToString(separator = " ") { stringify(t) }
            is Transform.Translate ->
                "translate(${t.horizontal} ${t.vertical})"
            is Transform.Scale ->
                if (t.pivotX != 0f || t.pivotY != 0f) {
                    stringify(t.split())
                } else {
                    "scale(${t.horizontal} ${t.vertical})"
                }
            is Transform.Rotate ->
                if (t.pivotX != 0f || t.pivotY != 0f) {
                    "rotate(${t.degrees} ${t.pivotX} ${t.pivotY})"
                } else {
                    "rotate(${t.degrees})"
                }
            is Transform.Skew ->
                if (t.horizontal != 0f && t.vertical != 0f) {
                    stringify(t.split())
                } else if (t.horizontal != 0f) {
                    "skewX(${t.horizontal})"
                } else {
                    "skewY(${t.vertical})"
                }
        }

        val element = XmlElement("g")
            .setAttribute("transform", stringify(transform))
        xmlAncestors.last().addContent(element)
        xmlAncestors.addLast(element)
    }

    override fun pop() {
        check(xmlAncestors.last() !== root) { "Cannot pop clip/transform. None exists." }
        xmlAncestors.removeLast()
    }

    /** Dump the SVG as an XML string. */
    public fun build(): String = root.toString()

    /**
     * If [paint] is a [Paint.Gradient] or [Paint.GradientAndStroke], adds a `<defs>` element
     * containing a gradient definition to the XML tree, and returns the id of that definition.
     *
     * If [paint] is not a gradient, then returns null.
     */
    private fun injectGradientDef(paint: Paint): String? {
        if (paint is Paint.GradientAndStroke) return injectGradientDef(paint.gradient)
        if (paint !is Paint.Gradient) return null

        val id = "g$gradientCount"
        gradientCount += 1

        val gradient = when (paint) {
            is Paint.Gradient.Linear -> XmlElement("linearGradient")
                .setAttribute("id", id)
                .setAttribute("gradientUnits", "userSpaceOnUse")
                .setAttribute("x1", paint.startX, formatter)
                .setAttribute("y1", paint.startY, formatter)
                .setAttribute("x2", paint.endX, formatter)
                .setAttribute("y2", paint.endY, formatter)
            is Paint.Gradient.Radial -> XmlElement("radialGradient")
                .setAttribute("id", id)
                .setAttribute("gradientUnits", "userSpaceOnUse")
                .setAttribute("cx", paint.centerX, formatter)
                .setAttribute("cy", paint.centerY, formatter)
                .setAttribute("r", paint.radius, formatter)
            else -> throw UnsupportedOperationException("`SvgKanvas` does not support `Sweep` gradients.")
        }

        paint.stops.forEach { (offset, color) ->
            val offsetPercent = (offset * 100).roundToInt()
            val stop = XmlElement("stop")
                .setAttribute("offset", "$offsetPercent%")
                .setColorAttributes("stop-color", "stop-opacity", color, formatter)
            gradient.addContent(stop)
        }

        val defs = XmlElement("defs")
            .addContent(gradient)

        xmlAncestors.last().addContent(defs)

        return id
    }
}
