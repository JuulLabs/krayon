package com.juul.krayon.sample

import com.juul.krayon.color.nextColor
import com.juul.krayon.color.white
import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.kanvas.HtmlCanvas
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.select
import com.juul.krayon.selection.selectAll
import kotlinx.browser.document
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.w3c.dom.HTMLCanvasElement
import kotlin.js.Promise
import kotlin.random.Random

private val data = MutableStateFlow(listOf(1f, 1f, 1f, 1f, 1f))
fun main() {
    // Kanvas
    val canvasElement = document.getElementById("canvas") as HTMLCanvasElement
    val kanvas = HtmlCanvas(canvasElement)

    // Selection
    val root = RootElement()
    val transform = root.appendChild(TransformElement(Transform.Scale()))

    GlobalScope.launch {
        awaitWebFonts("Roboto Slab")
        launch {
            while (true) {
                data.value = when (data.value.size) {
                    0 -> listOf(1f)
                    else -> when (Random.nextDouble()) {
                        in 0.0..0.1 -> ArrayList(data.value).also { it.removeAt(Random.nextInt(data.value.size)) }
                        in 0.1..0.2 -> ArrayList(data.value).also { it.add(1f) }
                        else -> ArrayList(data.value).also { it[Random.nextInt(data.value.size)] += 1f }
                    }
                }
                delay(16)
            }
        }
        launch {
            data.collect { data ->
                kanvas.drawRect(0f, 0f, kanvas.width, kanvas.height, kanvas.buildPaint(Paint.Fill(white)))

                transform.transform = Transform.Scale(vertical = 480 / (data.maxOrNull() ?: 1f), pivotY = 480f)
                val bars = transform.asSelection()
                    .selectAll(RectangleElement)
                    .data(data)

                bars.enter.append { _, _, _ -> RectangleElement(paint = Paint.Fill(Random.nextColor())) }
                bars.exit.select { _, _, _ -> parent?.removeChild(this) }

                bars.select { datum, index, _ ->
                    this as RectangleElement
                    left = index * 10f
                    right = left + 10f
                    bottom = kanvas.height
                    top = bottom - datum
                    this
                }

                root.applyTo(kanvas)
            }
        }
    }
}

private suspend fun awaitWebFonts(vararg fonts: String) {
    fonts.map { document.asDynamic().fonts.load("1em $it") as Promise<*> }
        .forEach { it.await() }
}
