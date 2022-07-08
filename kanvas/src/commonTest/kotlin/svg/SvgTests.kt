package com.juul.krayon.kanvas.svg

import com.juul.krayon.color.black
import com.juul.krayon.color.blue
import com.juul.krayon.color.green
import com.juul.krayon.color.lime
import com.juul.krayon.color.red
import com.juul.krayon.color.white
import com.juul.krayon.kanvas.Clip
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Paint.Gradient.Stop
import com.juul.krayon.kanvas.Paint.Stroke.Dash.Pattern
import com.juul.krayon.kanvas.serif
import com.juul.krayon.kanvas.withClip
import com.juul.krayon.kanvas.xml.ScientificFormatter
import kotlin.test.Test
import kotlin.test.assertEquals

class SvgTests {
    // TODO: Break this up into smaller, distinct tests.
    @Test
    fun output() {
        val expected = """
            <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1e2 1e2">
              <defs>
                <linearGradient id="g0" gradientUnits="userSpaceOnUse" x1="0" y1="0" x2="1e2" y2="1e2">
                  <stop offset="0%" stop-color="#ffffff" />
                  <stop offset="100%" stop-color="#000000" />
                </linearGradient>
              </defs>
              <path d="M 0 0 L 1e2 1e2 L 0 1e2 z" fill="url(#g0)" />
              <defs>
                <radialGradient id="g1" gradientUnits="userSpaceOnUse" cx="5e1" cy="5e1" r="7e1">
                  <stop offset="0%" stop-color="#ffffff" />
                  <stop offset="100%" stop-color="#000000" />
                </radialGradient>
              </defs>
              <path d="M 0 0 L 1e2 0 L 1e2 1e2 z" stroke-dasharray="2 1" stroke-width="1px" stroke="#00ff00" fill="url(#g1)" />
              <circle cx="1.6e1" cy="8.4e1" r="8" fill="#000000" />
              <defs>
                <clipPath id="c0">
                  <rect x="1.6e1" y="1.6e1" width="6.8e1" height="6.8e1" />
                </clipPath>
              </defs>
              <g clip-path="url(#c0)">
                <line x1="0" y1="1e2" x2="1e2" y2="0" stroke-width="2px" stroke="#ff0000" />
              </g>
              <text x="5e1" y="5e1" text-anchor="middle" font-family="&quot;Times New Roman&quot;, serif" font-size="1.6e1px" fill="#0000ff">
                Example
              </text>
              <path d="M -9.18485e-15 0 A 5e1 5e1 0 0 1 5e1 5e1" stroke-width="1px" stroke="#00ff00" fill="#008000" />
              <path d="M 5e1 5e1 A 2.5e1 2.5e1 0 0 1 5e1 1e2" stroke-dasharray="2 1" stroke-width="1px" stroke="#00ff00" fill="none" />
            </svg>
        """.trimIndent()
        val actual = run {
            val svg = SvgKanvas(width = 100f, height = 100f, ScientificFormatter(precision = 6))
            val blackFill = Paint.Fill(black)
            val redStroke = Paint.Stroke(red, width = 2f)
            val greenFillAndStroke = Paint.FillAndStroke(
                Paint.Fill(green),
                Paint.Stroke(lime, width = 1f),
            )
            val dashedGreenStroke = Paint.Stroke(lime, width = 1f, dash = Pattern(2f, 1f))
            val linearGradientPaint = Paint.Gradient.Linear(0f, 0f, 100f, 100f, Stop(0f, white), Stop(1f, black))
            val radialGradientPaint = Paint.Gradient.Radial(50f, 50f, 70f, Stop(0f, white), Stop(1f, black))
            val blueText = Paint.Text(blue, size = 16f, Paint.Text.Alignment.Center, Font("Times New Roman", serif))
            svg.drawPath("M0,0l100,100h-100z".toPath(), linearGradientPaint)
            svg.drawPath("M0,0h100v100z".toPath(), Paint.GradientAndStroke(radialGradientPaint, dashedGreenStroke))
            svg.drawCircle(16f, 84f, 8f, blackFill)
            svg.withClip(Clip.Rect(16f, 16f, 84f, 84f)) {
                drawLine(0f, 100f, 100f, 0f, redStroke)
            }
            svg.drawText("Example", 50f, 50f, blueText)
            svg.drawArc(-50f, 0f, 50f, 100f, 270f, 90f, greenFillAndStroke) // Quarter circle from top-left to center
            svg.drawArc(25f, 50f, 75f, 100f, 270f, 180f, dashedGreenStroke) // Half circle from center to bottom
            svg.build()
        }
        assertEquals(expected, actual)
    }
}
