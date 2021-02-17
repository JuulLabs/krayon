package com.juul.krayon.canvas.svg

import com.juul.krayon.canvas.Canvas
import com.juul.krayon.canvas.Clip
import com.juul.krayon.canvas.Paint
import com.juul.krayon.canvas.PathBuilder
import com.juul.krayon.canvas.Transform

public class SvgCanvas(
    override val width: Float,
    override val height: Float,
    private val indentSpaces: Int = 2
) : Canvas<Paint, PathString> {

    private val builder = StringBuilder()
    private var isClosed = false
    private var clipCount = 0
    private var indentation = 1

    init {
        builder.append("""<svg viewBox="0 0 $width $height" xmlns="http://www.w3.org/2000/svg">""")
    }

    private fun StringBuilder.indentNewLine() {
        appendLine()
        append("".padStart(indentation * indentSpaces))
    }

    override fun buildPaint(paint: Paint): Paint = paint

    override fun buildPath(actions: PathBuilder<*>.() -> Unit): PathString =
        PathStringBuilder().apply(actions).build()

    override fun drawArc(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        startAngle: Float,
        sweepAngle: Float,
        useCenter: Boolean,
        paint: Paint,
    ) {
        check(!isClosed)
        drawPath(buildPath { arcTo(left, top, right, bottom, startAngle, sweepAngle, forceMoveTo = true) }, paint)
    }

    override fun drawCircle(centerX: Float, centerY: Float, radius: Float, paint: Paint) {
        check(!isClosed)
        builder.indentNewLine()
        builder.append("""<circle cx="${centerX.toDouble()}" cy="${centerY.toDouble()}" r="${radius.toDouble()}" ${paint.toAttributeString()} />""")
    }

    override fun drawLine(startX: Float, startY: Float, endX: Float, endY: Float, paint: Paint) {
        check(!isClosed)
        builder.indentNewLine()
        builder.append("""<line x1="${startX.toDouble()}" y1="${startY.toDouble()}" x2="${endX.toDouble()}" y2="${endY.toDouble()}" ${paint.toAttributeString()} />""")
    }

    override fun drawOval(left: Float, top: Float, right: Float, bottom: Float, paint: Paint) {
        check(!isClosed)
        val rx = (right - left) / 2.0
        val ry = (bottom - top) / 2.0
        val cx = left + rx
        val cy = top + ry
        builder.indentNewLine()
        builder.append("""<ellipse cx="$cx" cy="$cy" rx="$rx" ry="$ry" ${paint.toAttributeString()} />""")
    }

    override fun drawPath(path: PathString, paint: Paint) {
        check(!isClosed)
        builder.indentNewLine()
        builder.append("""<path d="${path.string}" ${paint.toAttributeString()} />""")
    }

    override fun drawText(text: CharSequence, x: Float, y: Float, paint: Paint) {
        check(!isClosed)
        builder.indentNewLine()
        // STOPSHIP: text must be escaped or else we can break the SVG pretty easily
        builder.append("""<text ${paint.toAttributeString()}>$text</text>""")
    }

    override fun pushClip(clip: Clip<PathString>) {
        check(!isClosed)
        val pathName = "c$clipCount"
        clipCount += 1
        val pathImpl = when (clip) {
            is Clip.Rect -> """<rect x="${clip.left}" y="${clip.top}" width="${clip.right - clip.left}" height="${clip.bottom - clip.left}" />"""
            is Clip.Path -> """<path d="$clip" />"""
        }
        builder.indentNewLine()
        builder.append("""<defs><clipPath id="$pathName">$pathImpl</clipPath></defs>""")
        builder.indentNewLine()
        builder.append("""<g clip-path="url(#$pathName)">""")
        indentation += 1
    }

    override fun pushTransform(transform: Transform) {
        // TODO: Could likely be cleaned up
        check(!isClosed)
        fun stringify(t: Transform): String = when (t) {
            is Transform.InOrder ->
                t.transformations.joinToString(separator = " ") { stringify(t) }
            is Transform.Translate ->
                """translate(${t.horizontal} ${t.vertical})"""
            is Transform.Scale ->
                if (t.pivotX != 0f || t.pivotY != 0f) {
                    """translate(${t.pivotX} ${t.pivotY}) scale(${t.horizontal} ${t.vertical}) translate(${-t.pivotX} ${-t.pivotY})"""
                } else {
                    """scale(${t.horizontal} ${t.vertical})"""
                }
            is Transform.Rotate ->
                if (t.pivotX != 0f || t.pivotY != 0f) {
                    """rotate(${t.degrees} ${t.pivotX} ${t.pivotY})"""
                } else {
                    """rotate(${t.degrees})"""
                }
            is Transform.Skew ->
                if (t.horizontal != 0f && t.vertical != 0f) {
                    """skewX(${t.horizontal}) skewY(${t.vertical})"""
                } else if (t.horizontal != 0f) {
                    """skewX(${t.horizontal})"""
                } else {
                    """skewY(${t.vertical})"""
                }
        }
        builder.indentNewLine()
        builder.append("""<g transform="${stringify(transform)}" >""")
        indentation += 1
    }

    override fun pop() {
        check(!isClosed)
        check(indentation > 1)
        indentation -= 1
        builder.indentNewLine()
        builder.append("</g>")
    }

    public fun close() {
        builder.appendLine()
        builder.append("</svg>")
        isClosed = true
    }

    /** Calls [close] if this is not already closed. Returns the SVG string. */
    public fun build(): String {
        if (!isClosed) {
            close()
        }
        return builder.toString()
    }
}
