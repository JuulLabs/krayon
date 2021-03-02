package com.juul.krayon.kanvas

import androidx.test.core.app.ApplicationProvider
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.Test
import kotlin.test.assertEquals
import android.graphics.Paint as AndroidPaint

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class AndroidPaintTests {

    @Test
    fun checkFillPaintStyle() {
        val source = Paint.Fill(Color.black)
        val dest = source.toAndroid()
        assertEquals(dest.style, AndroidPaint.Style.FILL)
    }

    @Test
    fun checkStrokePaintStyle() {
        val source = Paint.Stroke(Color.black, width = 1f)
        val dest = source.toAndroid()
        assertEquals(dest.style, AndroidPaint.Style.STROKE)
    }

    @Test
    fun checkTextPaintStyle() {
        val source = Paint.Text(Color.black, size = 12f, Paint.Text.Alignment.Center, Font("Times New Roman"))
        val dest = source.toAndroid(ApplicationProvider.getApplicationContext())
        assertEquals(dest.style, AndroidPaint.Style.FILL)
    }
}
