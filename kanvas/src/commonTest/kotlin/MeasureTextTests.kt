package com.juul.krayon.kanvas

import com.juul.krayon.color.black
import kotlin.test.Test
import kotlin.test.assertEquals

class MeasureTextTests {

    private val paint = Paint.Text(black, size = 12f, Paint.Text.Alignment.Left, Font(sansSerif))

    @Test
    fun height_isSumOfAscentAndDescent() {
        val metrics = TextMetrics(width = 40f, ascent = 9f, descent = 3f)
        assertEquals(12f, metrics.height)
    }

    @Test
    fun measureText_funInterface_isInvokedWithArguments() {
        var seenText: CharSequence? = null
        var seenPaint: Paint.Text? = null
        val measurer = MeasureText { text, textPaint ->
            seenText = text
            seenPaint = textPaint
            TextMetrics(width = text.length.toFloat(), ascent = 1f, descent = 1f)
        }

        val result = measurer.measureText("hello", paint)

        assertEquals("hello", seenText)
        assertEquals(paint, seenPaint)
        assertEquals(5f, result.width)
        assertEquals(2f, result.height)
    }
}
