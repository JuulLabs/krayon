package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StatisticsTests {

    @Test
    fun variance_matchesD3() {
        assertEquals(2.5, listOf(1.0, 2.0, 3.0, 4.0, 5.0).variance()!!, absoluteTolerance = 1e-12)
    }

    @Test
    fun variance_ignoresNaN() {
        assertEquals(2.5, listOf(1.0, 2.0, Double.NaN, 3.0, 4.0, 5.0).variance()!!, absoluteTolerance = 1e-12)
    }

    @Test
    fun variance_requiresTwoValues() {
        assertNull(listOf(1.0).variance())
        assertNull(emptyList<Double>().variance())
    }

    @Test
    fun standardDeviation_matchesD3() {
        assertEquals(1.5811388300841898, listOf(1.0, 2.0, 3.0, 4.0, 5.0).standardDeviation()!!, absoluteTolerance = 1e-12)
    }

    @Test
    fun standardDeviation_requiresTwoValues() {
        assertNull(listOf(1.0).standardDeviation())
    }
}
