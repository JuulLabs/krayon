package com.juul.krayon.sample

import com.juul.krayon.color.white
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.HtmlCanvas
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.conflate
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import kotlin.time.ExperimentalTime

fun main() {
    val kanvas = HtmlCanvas(document.getElementById("canvas") as HTMLCanvasElement)

    GlobalScope.launch {
        val root = RootElement()

        @OptIn(ExperimentalTime::class)
        movingSineWave()
            .conflate()
            .collect { data ->
                lineChart(root, kanvas.width, kanvas.height, data)
                kanvas.drawColor(white)
                root.draw(kanvas)
            }
    }
}
