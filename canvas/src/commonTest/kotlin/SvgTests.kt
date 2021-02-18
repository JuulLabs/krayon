package com.juul.krayon.canvas

import com.juul.krayon.canvas.svg.SvgCanvas
import kotlin.test.Test
import kotlin.test.assertEquals

class SvgTests {
    // TODO: Break this up into smaller, distinct tests.
    @Test
    fun output() {
        val expected = """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100.0 100.0">
              <defs>
                <clipPath id="c0">
                  <rect x="16.0" y="16.0" width="68.0" height="68.0" />
                </clipPath>
              </defs>
              <g clip-path="url(#c0)">
                <line x1="0.0" y1="0.0" x2="100.0" y2="100.0" stroke-linejoin="round" stroke="#ff0000" stroke-width="2.0px" />
              </g>
              <circle cx="16.0" cy="16.0" r="8.0" stroke-linejoin="round" stroke="#000000" stroke-width="4.0px" />
              <text x="50.0" y="50.0" text-anchor="middle" font-family="&quot;Times New Roman&quot;, serif" font-size="16.0px" fill="#0000ff">
                Example
              </text>
            </svg>
        """.trimIndent()
        val actual = run {
            val svg = SvgCanvas(width = 100f, height = 100f)
            val paint = Paint.Stroke(Color.red, width = 2f, join = Paint.Stroke.Join.Round)
            val textPaint = Paint.Text(Color.blue, size = 16f, alignment = Paint.Text.Alignment.Center, Font("Times New Roman", serif))
            svg.withClip(Clip.Rect(16f, 16f, 84f, 84f)) {
                drawLine(0f, 0f, 100f, 100f, paint)
            }
            svg.drawCircle(16f, 16f, 8f, paint.copy(color = Color.black, width = 4f))
            svg.drawText("Example", 50f, 50f, textPaint)
            svg.build()
        }
        assertEquals(expected, actual)
    }
}
