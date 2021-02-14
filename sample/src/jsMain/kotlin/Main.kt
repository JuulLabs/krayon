package com.juul.krayon.sample

import com.juul.krayon.canvas.HtmlCanvas
import com.juul.krayon.chart.render.BarChartRenderer
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

fun main() {
    val canvasElement = document.getElementById("canvas") as HTMLCanvasElement
    val kanvas = HtmlCanvas(canvasElement)
    val renderer = BarChartRenderer()
    renderer.render(getRandomData(), kanvas)
    canvasElement.onclick = {
        renderer.render(getRandomData(), kanvas)
    }
}
