package com.juul.krayon.kanvas

import com.juul.krayon.color.black
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class HtmlTextMeasurementTests {

    private val measurement = HtmlTextMeasurement()
    private val paint = Paint.Text(black, size = 24f, Paint.Text.Alignment.Left, Font(sansSerif))

    @Test
    fun emptyString_hasZeroWidth() {
        assertEquals(0f, measurement.measureText("", paint).width)
    }

    @Test
    fun nonEmptyString_hasPositiveWidth() {
        val metrics = measurement.measureText("Hello", paint)
        assertTrue(metrics.width > 0f, "expected positive width, was ${metrics.width}")
        assertTrue(metrics.height >= 0f, "expected non-negative height, was ${metrics.height}")
    }

    @Test
    fun longerString_isWider() {
        val short = measurement.measureText("i", paint).width
        val long = measurement.measureText("iiiiiiiiii", paint).width
        assertTrue(long > short, "expected '$long' > '$short'")
    }
}
