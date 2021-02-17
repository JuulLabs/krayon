package com.juul.krayon.canvas

import com.juul.krayon.canvas.svg.SvgCanvas
import kotlin.test.Test
import kotlin.test.assertEquals

class SvgTests {
    @Test
    fun output() {
        val expected = """
            <svg viewBox="0 0 100.0 100.0" xmlns="http://www.w3.org/2000/svg">
              <defs><clipPath id="c0"><rect x="16.0" y="16.0" width="68.0" height="68.0" /></clipPath></defs>
              <g clip-path="url(#c0)">
                <line x1="0.0" y1="0.0" x2="100.0" y2="100.0" stroke="#ff0000" stroke-width="2.0px" stroke-linejoin="round" />
              </g>
              <circle cx="16.0" cy="16.0" r="8.0" stroke="#0" stroke-width="4.0px" stroke-linejoin="round" />
            </svg>
        """.trimIndent()
        val actual = run {
            val svg = SvgCanvas(width = 100f, height = 100f)
            val paint = Paint.Stroke(Color.red, width = 2f, join = Paint.Stroke.Join.Round)
            svg.withClip(Clip.Rect(16f, 16f, 84f, 84f)) {
                drawLine(0f, 0f, 100f, 100f, paint)
            }
            svg.drawCircle(16f, 16f, 8f, paint.copy(color = Color.black, width = 4f))
            svg.build()
        }
        assertEquals(expected, actual)
    }
}
