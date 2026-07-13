package com.juul.krayon.scale

import kotlin.test.Test
import kotlin.test.assertEquals

class ThresholdScaleTests {

    @Test
    fun threshold_mapsBySortedBoundaries() {
        val scale = thresholdScale(domain = listOf(0f, 1f), range = listOf("a", "b", "c"))
        assertEquals("a", scale.scale(-1f))
        assertEquals("b", scale.scale(0f))
        assertEquals("b", scale.scale(0.5f))
        assertEquals("c", scale.scale(1f))
        assertEquals("c", scale.scale(2f))
    }

    @Test
    fun threshold_invertExtent() {
        val scale = thresholdScale(domain = listOf(0f, 1f), range = listOf("a", "b", "c"))
        assertEquals(Pair(null, 0f), scale.invertExtent("a"))
        assertEquals(Pair(0f, 1f), scale.invertExtent("b"))
        assertEquals(Pair(1f, null), scale.invertExtent("c"))
    }

    @Test
    fun threshold_worksWithNonNumericDomain() {
        val scale = thresholdScale(domain = listOf("m"), range = listOf("before", "after"))
        assertEquals("before", scale.scale("a"))
        assertEquals("after", scale.scale("z"))
    }
}
