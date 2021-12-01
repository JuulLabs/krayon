package com.juul.krayon.sample

import com.juul.krayon.color.darkSlateBlue
import com.juul.krayon.color.steelBlue
import com.juul.krayon.kanvas.HtmlCanvas
import com.juul.krayon.kanvas.Paint.Stroke
import com.juul.krayon.kanvas.Paint.Stroke.Dash.Pattern
import com.juul.krayon.scale.domain
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import com.juul.krayon.shape.line
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import kotlin.math.PI
import kotlin.math.sin

private data class Point(val x: Float, val y: Float)

fun main() {
    val canvasElement = document.getElementById("canvas") as HTMLCanvasElement
    val kanvas = HtmlCanvas(canvasElement)

    // Generate a sine wave, with missing data every fifth point
    val samples = 50
    val data = (0 until samples).map { i ->
        val proportion = i.toFloat() / (samples - 1)
        val x = (2 * PI * proportion).toFloat()
        Point(x, y = sin(x)).takeUnless { i % 5 == 0 }
    }

    val x = scale().domain(0f, 2 * PI.toFloat()).range(10f, kanvas.width - 10)
    val y = scale().domain(-1f, 1f).range(kanvas.height - 10, 0f + 10)

    val line = line<Point>()
        .x { (p) -> x.scale(p.x) }
        .y { (p) -> y.scale(p.y) }

    kanvas.drawPath(kanvas.buildPath(line.render(data.filterNotNull())), kanvas.buildPaint(Stroke(darkSlateBlue, 0.5f, dash = Pattern(5f, 5.5f))))
    kanvas.drawPath(kanvas.buildPath(line.render(data)), kanvas.buildPaint(Stroke(steelBlue, 1f)))
}
