package com.juul.krayon.scale

import kotlin.math.abs
import kotlin.math.ln1p
import kotlin.test.Test
import kotlin.test.assertEquals

class SymlogScaleTests {

    @Test
    fun symlog_mapsZeroToCenter() {
        val scale = symlogScale(domain = listOf(-100f, 100f), range = listOf(-1f, 1f))
        assertEquals(0f, scale.scale(0f), absoluteTolerance = 1e-5f)
        assertEquals(-1f, scale.scale(-100f), absoluteTolerance = 1e-5f)
        assertEquals(1f, scale.scale(100f), absoluteTolerance = 1e-5f)
    }

    @Test
    fun symlog_matchesTransform() {
        val scale = symlogScale(domain = listOf(-100f, 100f), range = listOf(-1f, 1f))
        // Normalized position of x=9 under transform sign(x)*ln1p(|x|).
        val bound = ln1p(100f)
        val expected = ln1p(9f) / bound
        assertEquals(expected, scale.scale(9f), absoluteTolerance = 1e-4f)
    }

    @Test
    fun symlog_respectsConstant() {
        val scale = symlogScale(domain = listOf(0f, 100f), range = listOf(0f, 1f), constant = 5f)
        val bound = ln1p(abs(100f / 5f))
        val expected = ln1p(abs(50f / 5f)) / bound
        assertEquals(expected, scale.scale(50f), absoluteTolerance = 1e-4f)
    }

    @Test
    fun symlog_invertRoundTrips() {
        val scale = symlogScale(domain = listOf(-100f, 100f), range = listOf(-1f, 1f))
        assertEquals(9f, scale.invert(scale.scale(9f)), absoluteTolerance = 1e-2f)
        assertEquals(0f, scale.invert(0f), absoluteTolerance = 1e-4f)
    }
}
