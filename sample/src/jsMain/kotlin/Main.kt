package com.juul.krayon.sample

import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.Font
import com.juul.krayon.canvas.HtmlCanvas
import com.juul.krayon.canvas.Paint
import com.juul.krayon.canvas.serif
import com.juul.krayon.chart.render.BarChartRenderer
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Promise

fun main() {
    val canvasElement = document.getElementById("canvas") as HTMLCanvasElement
    val kanvas = HtmlCanvas(canvasElement)
    val renderer = BarChartRenderer()

    fun render() {
        // TODO: should probably add a "clear" function of "fillWithColor" or something.
        kanvas.drawRect(0f, 0f, kanvas.width, kanvas.height, kanvas.buildPaint(Paint.Fill(Color.white)))
        renderer.render(getRandomData(), kanvas)
        val textPaint = Paint.Text(Color.black, 18f, Paint.Text.Alignment.Left, Font("Roboto Slab", serif))
        kanvas.drawText("This is a test", 32f, 32f, textPaint)
    }

    GlobalScope.launch {
        awaitWebFonts("Roboto Slab")
        render()
        canvasElement.onclick = { render() }
    }
}

private suspend fun awaitWebFonts(vararg fonts: String) {
    fonts.map { document.asDynamic().fonts.load("1em $it") as Promise<*> }
        .forEach { it.await() }
}
