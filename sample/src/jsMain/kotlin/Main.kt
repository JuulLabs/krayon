package com.juul.krayon.sample

import com.juul.krayon.color.white
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.HtmlCanvas
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.w3c.dom.HTMLCanvasElement
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
    val kanvas = HtmlCanvas(document.getElementById("canvas") as HTMLCanvasElement)
    val root = RootElement()

    movingSineWave()
        .conflate()
        .onEach { data ->
            lineChart(root, kanvas.width, kanvas.height, data)
            kanvas.drawColor(white)
            root.draw(kanvas)
        }.launchIn(GlobalScope)
}
