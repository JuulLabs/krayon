package com.juul.krayon.sample

import com.juul.krayon.chart.render.BarChartRenderer
import com.juul.krayon.color.black
import com.juul.krayon.color.white
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.HtmlCanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.serif
import com.juul.krayon.scale.range
import com.juul.krayon.scale.scale
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Promise
import kotlin.math.sin

private data class Point(
    val x: Double,
    val y: Double,
)

fun main() {
    val canvasElement = document.getElementById("canvas") as HTMLCanvasElement
    val kanvas = HtmlCanvas(canvasElement)
    val renderer = BarChartRenderer()

    fun render() {
        // TODO: should probably add a "clear" function of "fillWithColor" or something.
        kanvas.drawRect(0f, 0f, kanvas.width, kanvas.height, kanvas.buildPaint(Paint.Fill(white)))
        renderer.render(getRandomData(), kanvas)
        val textPaint = Paint.Text(black, 18f, Paint.Text.Alignment.Left, Font("Roboto Slab", serif))
        kanvas.drawText("This is a test", 32f, 32f, textPaint)
    }

    GlobalScope.launch {
        awaitWebFonts("Roboto Slab")
        render()
        canvasElement.onclick = { render() }
    }

    val data = (0 until 40).map { i ->
        Point(x = i / 39.0, y = (sin(i / 3.0) + 2) / 4)
            .takeUnless { i % 5 == 0 }
    }

    val x = scale().range(0f, kanvas.width)
    val y = scale().range(kanvas.height, 0f)
}

private suspend fun awaitWebFonts(vararg fonts: String) {
    fonts.map { document.asDynamic().fonts.load("1em $it") as Promise<*> }
        .forEach { it.await() }
}
