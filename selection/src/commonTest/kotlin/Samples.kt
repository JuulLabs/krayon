package com.juul.krayon.selection

import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.svg.SvgKanvas
import com.juul.krayon.kanvas.xml.ScientificFormatter
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class Samples {

    @Test
    fun svgTest() = runTest {
        val canvas = SvgKanvas(width = 500f, height = 100f, formatter = ScientificFormatter(4))
        val root = RootElement()
        root.asSelection()
            .selectAll(CircleElement)
            .data(listOf(100f, 250f, 400f))
            .join(
                onEnter = {
                    append(CircleElement)
                        .each { centerY = 50f }
                        .each { radius = 1f }
                },
            ).each { (datum) -> centerX = datum }
        root.draw(canvas)
        assertEquals(
            """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 5e2 1e2">
              <circle cx="1e2" cy="5e1" r="1" fill="#000000" />
              <circle cx="2.5e2" cy="5e1" r="1" fill="#000000" />
              <circle cx="4e2" cy="5e1" r="1" fill="#000000" />
            </svg>
            """.trimIndent(),
            canvas.build()
        )
    }
}
