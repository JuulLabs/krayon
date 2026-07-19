package com.juul.krayon.kanvas

import com.juul.krayon.color.black
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CoreTextMeasurementTests {

    private val paint = Paint.Text(black, size = 24f, Paint.Text.Alignment.Left, Font(sansSerif))

    @Test
    fun emptyString_hasZeroWidth() {
        assertEquals(0f, CoreTextMeasurement.measureText("", paint).width)
    }

    @Test
    fun nonEmptyString_hasPositiveSize() {
        val metrics = CoreTextMeasurement.measureText("Hello", paint)
        assertTrue(metrics.width > 0f, "expected positive width, was ${metrics.width}")
        assertTrue(metrics.ascent > 0f, "expected positive ascent, was ${metrics.ascent}")
        assertTrue(metrics.descent > 0f, "expected positive descent, was ${metrics.descent}")
        assertEquals(metrics.ascent + metrics.descent, metrics.height)
    }

    @Test
    fun longerString_isWider() {
        val short = CoreTextMeasurement.measureText("i", paint).width
        val long = CoreTextMeasurement.measureText("iiiiiiiiii", paint).width
        assertTrue(long > short, "expected '$long' > '$short'")
    }

    @Test
    fun verticalMetrics_areIndependentOfText() {
        val a = CoreTextMeasurement.measureText("a", paint)
        val b = CoreTextMeasurement.measureText("longer text", paint)
        assertEquals(a.ascent, b.ascent)
        assertEquals(a.descent, b.descent)
    }
}
