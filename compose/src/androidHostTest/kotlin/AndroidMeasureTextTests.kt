package com.juul.krayon.compose

import com.juul.krayon.color.black
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.sansSerif
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(manifest = Config.NONE, sdk = [35])
class AndroidMeasureTextTests {

    private val measurement = textMeasurement()
    private val paint = Paint.Text(black, size = 24f, Paint.Text.Alignment.Left, Font(sansSerif))

    @Test
    fun emptyString_hasZeroWidth() {
        assertEquals(0f, measurement.measureText("", paint).width)
    }

    @Test
    fun nonEmptyString_hasPositiveSize() {
        val metrics = measurement.measureText("Hello", paint)
        assertTrue(metrics.width > 0f, "expected positive width, was ${metrics.width}")
        assertTrue(metrics.ascent > 0f, "expected positive ascent, was ${metrics.ascent}")
        assertTrue(metrics.descent > 0f, "expected positive descent, was ${metrics.descent}")
        assertEquals(metrics.ascent + metrics.descent, metrics.height)
    }

    @Test
    fun longerString_isWider() {
        val short = measurement.measureText("i", paint).width
        val long = measurement.measureText("iiiiiiiiii", paint).width
        assertTrue(long > short, "expected '$long' > '$short'")
    }

    @Test
    fun verticalMetrics_areIndependentOfText() {
        val a = measurement.measureText("a", paint)
        val b = measurement.measureText("longer text", paint)
        assertEquals(a.ascent, b.ascent)
        assertEquals(a.descent, b.descent)
    }
}
