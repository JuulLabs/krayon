package com.juul.krayon.sample

import com.juul.krayon.canvas.Color
import com.juul.krayon.canvas.HtmlCanvas
import com.juul.krayon.canvas.Paint
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

fun main() {
    val kanvas = HtmlCanvas(document.getElementById("canvas") as HTMLCanvasElement)
    kanvas.drawLine(0f, 0f, kanvas.width, kanvas.height, Paint.Stroke(Color.black, 1f))
}
