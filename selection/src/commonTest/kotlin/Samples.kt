package com.juul.krayon.selection

import com.juul.krayon.color.black
import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.descendents
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.svg.SvgKanvas
import com.juul.krayon.kanvas.xml.ScientificFormatter
import com.juul.tuulbox.test.runTest
import kotlin.test.Test

class Samples {

    @Test
    fun svgTest() = runTest {
        val canvas = SvgKanvas(width = 500f, height = 100f)
        val root = RootElement()
        root.selection()
            .selectAll { _, _, _ -> descendents.filter { it is CircleElement }.toList() }
            .data(listOf(100, 250, 400))
            .enter.select { datum, index, nodes ->
                CircleElement(
                    centerX = datum.toFloat(),
                    centerY = 50f,
                    radius = 1f,
                    paint = Paint.Fill(black)
                ).also { circle ->
                    appendChild(circle)
                }
            }
        root.applyTo(canvas)
        val svg = canvas.build()
        println(svg)
    }
}
