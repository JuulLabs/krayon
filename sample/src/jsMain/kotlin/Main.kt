package com.juul.krayon.sample

import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.element.view.attachAdapter
import kotlinx.browser.document
import org.w3c.dom.HTMLCanvasElement
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
fun main() {
    val canvas = document.getElementById("canvas") as HTMLCanvasElement
    canvas.attachAdapter(
        ElementViewAdapter(
            dataSource = movingSineWave(),
            updater = ::lineChart
        )
    )
}
