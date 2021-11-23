package com.juul.krayon.selection

import com.juul.krayon.element.CircleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.kanvas.svg.SvgKanvas
import com.juul.tuulbox.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class Samples {

    @Test
    fun svgTest() = runTest {
        val canvas = SvgKanvas(width = 500f, height = 100f)
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
        root.applyTo(canvas)
        assertEquals(
            """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 500.0 100.0">
              <circle cx="100.0" cy="50.0" r="1.0" fill="#000000" />
              <circle cx="250.0" cy="50.0" r="1.0" fill="#000000" />
              <circle cx="400.0" cy="50.0" r="1.0" fill="#000000" />
            </svg>
            """.trimIndent(),
            canvas.build()
        )
    }
}
