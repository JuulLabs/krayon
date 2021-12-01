package com.juul.krayon.sample

import com.juul.krayon.kanvas.HtmlCanvas
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement

fun main() {
    val kanvas = HtmlCanvas(document.getElementById("canvas") as HTMLCanvasElement)
    kanvas.renderSineWave()
}
