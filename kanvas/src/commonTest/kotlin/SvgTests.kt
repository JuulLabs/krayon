package com.juul.krayon.kanvas

import com.juul.krayon.color.Color
import com.juul.krayon.kanvas.Paint.Stroke.Dash.Pattern
import com.juul.krayon.kanvas.svg.SvgKanvas
import kotlin.test.Test
import kotlin.test.assertEquals

class SvgTests {
    // TODO: Break this up into smaller, distinct tests.
    @Test
    fun output() {
        val expected = """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 100.0 100.0">
              <circle cx="16.0" cy="84.0" r="8.0" fill="#000000" />
              <defs>
                <clipPath id="c0">
                  <rect x="16.0" y="16.0" width="68.0" height="68.0" />
                </clipPath>
              </defs>
              <g clip-path="url(#c0)">
                <line x1="0.0" y1="100.0" x2="100.0" y2="0.0" stroke-width="2.0px" stroke="#ff0000" />
              </g>
              <text x="50.0" y="50.0" text-anchor="middle" font-family="&quot;Times New Roman&quot;, serif" font-size="16.0px" fill="#0000ff">
                Example
              </text>
              <path d="M -9.184851E-15 0.0 A 50.0 50.0 0 0 1 50.0 50.0" stroke-width="1.0px" stroke="#00ff00" fill="none" />
              <path d="M 50.0 50.0 A 25.0 25.0 0 0 1 50.0 100.0" stroke-dasharray="2.0 1.0" stroke-width="1.0px" stroke="#00ff00" fill="none" />
            </svg>
        """.trimIndent()
        val actual = run {
            val svg = SvgKanvas(width = 100f, height = 100f)
            val blackFill = Paint.Fill(Color.black)
            val redStroke = Paint.Stroke(Color.red, width = 2f)
            val greenStroke = Paint.Stroke(Color.green, width = 1f)
            val dashedGreenStroke = greenStroke.copy(dash = Pattern(2f, 1f))
            val blueText = Paint.Text(Color.blue, size = 16f, Paint.Text.Alignment.Center, Font("Times New Roman", serif))
            svg.drawCircle(16f, 84f, 8f, blackFill)
            svg.withClip(Clip.Rect(16f, 16f, 84f, 84f)) {
                drawLine(0f, 100f, 100f, 0f, redStroke)
            }
            svg.drawText("Example", 50f, 50f, blueText)
            svg.drawArc(-50f, 0f, 50f, 100f, 270f, 90f, greenStroke) // Quarter circle from top-left to center
            svg.drawArc(25f, 50f, 75f, 100f, 270f, 180f, dashedGreenStroke) // Half circle from center to bottom
            svg.build()
        }
        assertEquals(expected, actual)
    }
}
