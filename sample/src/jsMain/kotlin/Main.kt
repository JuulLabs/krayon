package com.juul.krayon.sample

import com.juul.krayon.color.blue
import com.juul.krayon.color.red
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.view.ElementViewAdapter
import com.juul.krayon.element.view.UpdateElement
import com.juul.krayon.element.view.attachAdapter
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.selection.append
import com.juul.krayon.selection.asSelection
import com.juul.krayon.selection.data
import com.juul.krayon.selection.each
import com.juul.krayon.selection.join
import com.juul.krayon.selection.selectAll
import kotlinx.browser.document
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import org.w3c.dom.HTMLCanvasElement
import org.w3c.dom.HTMLInputElement
import kotlin.math.PI

private val arcs = listOf(1f, 2f, 3f, 4f, 5f, 6f)

fun main() {
    setupLineChart()
    setupPieChart()
    setupInteractiveChart()
}

private fun setupLineChart() {
    (document.getElementById("line-canvas") as HTMLCanvasElement)
        .attachAdapter(
            ElementViewAdapter(
                dataSource = movingSineWave(),
                updater = ::lineChart
            )
        )
}

private fun setupPieChart() {
    fun configure(id: String, min: Float, max: Float, state: MutableStateFlow<Float>) {
        val input = document.getElementById(id) as HTMLInputElement
        input.min = min.toString()
        input.max = max.toString()
        input.step = ((max - min) / 1000f).toString()
        input.value = state.value.toString()
        input.oninput = { _ ->
            state.value = input.value.toFloatOrNull() ?: 0f
            Unit // Apparently assignment doesn't return a `dynamic`, which the function interface requires.
        }
    }

    val startAngle = MutableStateFlow(0f)
    val endAngle = MutableStateFlow((PI * 2).toFloat())
    val cornerRadius = MutableStateFlow(0f)
    val paddingAngle = MutableStateFlow(0f)
    val innerRadius = MutableStateFlow(0f)

    configure("pie-start-angle", min = 0f, max = PI.toFloat(), state = startAngle)
    configure("pie-end-angle", min = PI.toFloat(), max = (2 * PI).toFloat(), state = endAngle)
    configure("pie-corner-radius", min = 0f, max = 32f, state = cornerRadius)
    configure("pie-padding-angle", min = 0f, max = 0.1f, state = paddingAngle)
    configure("pie-inner-radius", min = 0f, max = 192f, state = innerRadius)

    // Convert state into a flow for consumption by the element view adapter.
    val charts = combine(startAngle, endAngle, cornerRadius, paddingAngle, innerRadius) { startAngle, endAngle, cornerRadius, paddingAngle, innerRadius ->
        PieChart(arcs, startAngle, endAngle, cornerRadius, paddingAngle, innerRadius)
    }

    (document.getElementById("pie-canvas") as HTMLCanvasElement).attachAdapter(
        ElementViewAdapter(
            dataSource = charts,
            updater = ::pieChart
        )
    )
}

private fun setupInteractiveChart() {
    val isToggled = MutableStateFlow(listOf(false, false, false))
    val render = UpdateElement<List<Boolean>> { root, width, height, data ->
        root.asSelection()
            .selectAll(TransformElement)
            .data(data)
            .join {
                append(TransformElement).each { (_, i) ->
                    val child = CircleElement().apply {
                        radius = 100f
                        onClick = {
                            isToggled.value = isToggled.value
                                .toBooleanArray()
                                .apply { this[i] = !this[i] }
                                .toList()
                        }
                    }
                    appendChild(child)
                }
            }
            .each { (d, i) ->
                transform = Transform.Translate(horizontal = (width / 5) * (1 + i), vertical = height / 2)
                (children.single() as CircleElement).paint = Paint.Fill((if (d) blue else red).copy(alpha = 128))
            }
    }

    val canvasElement = document.getElementById("interaction-canvas") as HTMLCanvasElement
    canvasElement.attachAdapter(ElementViewAdapter(isToggled, render))
}
