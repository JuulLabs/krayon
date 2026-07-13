package com.juul.krayon.scale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class BandScaleTests {

    @Test
    fun band_withoutPadding_dividesRangeEvenly() {
        val scale = bandScale(domain = listOf("a", "b", "c"), range = listOf(0f, 120f))
        assertEquals(0f, scale.scale("a"), absoluteTolerance = 1e-4f)
        assertEquals(40f, scale.scale("b"), absoluteTolerance = 1e-4f)
        assertEquals(80f, scale.scale("c"), absoluteTolerance = 1e-4f)
        assertEquals(40f, scale.bandwidth, absoluteTolerance = 1e-4f)
        assertEquals(40f, scale.step, absoluteTolerance = 1e-4f)
    }

    @Test
    fun band_withPadding_matchesD3() {
        // d3: scaleBand().domain(["a","b","c"]).range([0,100]).padding(0.1)
        val scale = bandScale(domain = listOf("a", "b", "c"), range = listOf(0f, 100f)).padding(0.1f)
        assertEquals(3.2258f, scale.scale("a"), absoluteTolerance = 1e-3f)
        assertEquals(35.4839f, scale.scale("b"), absoluteTolerance = 1e-3f)
        assertEquals(67.7419f, scale.scale("c"), absoluteTolerance = 1e-3f)
        assertEquals(29.0323f, scale.bandwidth, absoluteTolerance = 1e-3f)
        assertEquals(32.2581f, scale.step, absoluteTolerance = 1e-3f)
    }

    @Test
    fun band_reversedRange_ordersFromTop() {
        val scale = bandScale(domain = listOf("a", "b", "c"), range = listOf(120f, 0f))
        assertEquals(80f, scale.scale("a"), absoluteTolerance = 1e-4f)
        assertEquals(40f, scale.scale("b"), absoluteTolerance = 1e-4f)
        assertEquals(0f, scale.scale("c"), absoluteTolerance = 1e-4f)
    }

    @Test
    fun band_round_producesIntegerPositions() {
        val scale = bandScale(domain = listOf("a", "b", "c"), range = listOf(0f, 100f)).round(true)
        // step = floor(100/3) = 33, start = round((100 - 33*3)/2) = round(0.5) = 1
        assertEquals(1f, scale.scale("a"), absoluteTolerance = 1e-4f)
        assertEquals(34f, scale.scale("b"), absoluteTolerance = 1e-4f)
        assertEquals(67f, scale.scale("c"), absoluteTolerance = 1e-4f)
        assertEquals(33f, scale.step, absoluteTolerance = 1e-4f)
        assertEquals(33f, scale.bandwidth, absoluteTolerance = 1e-4f)
    }

    @Test
    fun band_unknownInput_throws() {
        val scale = bandScale(domain = listOf("a", "b"), range = listOf(0f, 100f))
        assertFailsWith<IllegalArgumentException> { scale.scale("z") }
    }
}
