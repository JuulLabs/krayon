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
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
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
    val canvasElement = document.getElementById("canvas") as HTMLCanvasElement
    val kanvas = HtmlCanvas(canvasElement)

    val root = TransformElement()

    GlobalScope.launch {
        awaitWebFonts("Roboto Slab")
        launch {
            while (true) {
                data.value = newData()
                delay(16)
            }
        }

        data.collect { data ->
            kanvas.drawRect(0f, 0f, kanvas.width, kanvas.height, kanvas.buildPaint(Paint.Fill(white)))

            root.transform = Transform.Scale(vertical = 480 / (data.maxOrNull() ?: 1f), pivotY = 480f)

            root.asSelection()
                .selectAll(RectangleElement)
                .data(data)
                .join(
                    onEnter = {
                        append(RectangleElement)
                            .each { paint = Paint.Fill(Random.nextColor()) }
                            .each { bottom = kanvas.height }
                    }
                ).each { left = it.index * 10f }
                .each { right = left + 10f }
                .each { top = bottom - it.datum }

            root.draw(kanvas)
        }
    }
}

private fun newData(): List<Float> = when (data.value.size) {
    0 -> listOf(1f)
    in 1..5 -> when (Random.nextDouble()) {
        in 0.0..0.2 -> ArrayList(data.value).also { it.add(1f) }
        else -> ArrayList(data.value).also { it[Random.nextInt(data.value.size)] += 1f }
    }
    else -> when (Random.nextDouble()) {
        in 0.0..0.1 -> ArrayList(data.value).also { it.removeAt(Random.nextInt(data.value.size)) }
        in 0.1..0.2 -> ArrayList(data.value).also { it.add(1f) }
        else -> ArrayList(data.value).also { it[Random.nextInt(data.value.size)] += 1f }
    }
}

private suspend fun awaitWebFonts(vararg fonts: String) {
    fonts.map { document.asDynamic().fonts.load("1em $it") as Promise<*> }
        .forEach { it.await() }
}
