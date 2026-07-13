package com.juul.krayon.kanvas

import kotlinx.browser.document
import org.w3c.dom.CanvasRenderingContext2D
import org.w3c.dom.HTMLCanvasElement

private val WHITESPACE = Regex("\\s")

/** The CSS `font` shorthand string that draws/measures this [Paint.Text]. */
internal fun Paint.Text.cssFont(): String {
    val size = "${size}px"
    val name = font.names.joinToString { family ->
        if (WHITESPACE in family) "\"$family\"" else family
    }
    return "$size $name"
}

/**
 * Standalone [MeasureText] implementation backed by an offscreen HTML canvas. Useful for measuring
 * text during layout, when an [HtmlKanvas] is not available.
 *
 * A dedicated offscreen rendering context is used so that measurement never mutates the state of any
 * canvas being drawn to.
 */
public class HtmlTextMeasurement : MeasureText {

    private val context: CanvasRenderingContext2D =
        (document.createElement("canvas") as HTMLCanvasElement).getContext("2d") as CanvasRenderingContext2D

    override fun measureText(text: CharSequence, paint: Paint.Text): TextMetrics {
        context.font = paint.cssFont()
        val metrics = context.measureText(text.toString())
        val dynamic = metrics.asDynamic()
        val ascent = (dynamic.fontBoundingBoxAscent ?: dynamic.actualBoundingBoxAscent ?: 0.0) as Double
        val descent = (dynamic.fontBoundingBoxDescent ?: dynamic.actualBoundingBoxDescent ?: 0.0) as Double
        return TextMetrics(
            width = metrics.width.toFloat(),
            ascent = ascent.toFloat(),
            descent = descent.toFloat(),
        )
    }
}
