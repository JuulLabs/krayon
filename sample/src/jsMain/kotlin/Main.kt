package com.juul.krayon.sample

import com.juul.krayon.color.white
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.HtmlCanvas
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import org.w3c.dom.HTMLCanvasElement
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
    val kanvas = HtmlCanvas(document.getElementById("canvas") as HTMLCanvasElement)
    val root = RootElement()

    movingSineWave()
        .onEach { data ->
            lineChart(root, kanvas.width, kanvas.height, data)
            kanvas.drawColor(white)
            root.draw(kanvas)
            // It's important to delay when consuming a tight-loop data source, or JS's
            // dispatcher will hold onto the thread for too long and prevent rendering.
            delay(1.milliseconds)
        }.launchIn(GlobalScope)
}
