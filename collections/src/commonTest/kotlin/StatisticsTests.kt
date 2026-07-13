package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class StatisticsTests {

    @Test
    fun sum_ofValues_matchesD3() {
        assertEquals(15.0, listOf(1.0, 2.0, 3.0, 4.0, 5.0).sum { it })
    }

    @Test
    fun sum_ignoresNullAndNaN() {
        assertEquals(6.0, listOf(1.0, Double.NaN, 2.0, 3.0).sum { it })
        assertEquals(3.0, listOf(1.0, null, 2.0).sum { it })
    }

    @Test
    fun sum_ofEmpty_isZero() {
        assertEquals(0.0, emptyList<Double>().sum { it })
    }

    @Test
    fun mean_matchesD3() {
        assertEquals(3.0, listOf(1.0, 2.0, 3.0, 4.0, 5.0).mean { it })
    }

    @Test
    fun mean_ofEmpty_isNull() {
        assertNull(emptyList<Double>().mean { it })
        assertNull(listOf(Double.NaN).mean { it })
    }

    @Test
    fun count_ignoresNullAndNaN() {
        assertEquals(3, listOf(1.0, Double.NaN, 2.0, null, 3.0).count { it })
    }

    @Test
    fun variance_matchesD3() {
        assertEquals(2.5, listOf(1.0, 2.0, 3.0, 4.0, 5.0).variance { it }!!, absoluteTolerance = 1e-12)
    }

    @Test
    fun variance_ignoresNaN() {
        assertEquals(2.5, listOf(1.0, 2.0, Double.NaN, 3.0, 4.0, 5.0).variance { it }!!, absoluteTolerance = 1e-12)
    }

    @Test
    fun variance_requiresTwoValues() {
        assertNull(listOf(1.0).variance { it })
        assertNull(emptyList<Double>().variance { it })
    }

    @Test
    fun deviation_matchesD3() {
        assertEquals(1.5811388300841898, listOf(1.0, 2.0, 3.0, 4.0, 5.0).deviation { it }!!, absoluteTolerance = 1e-12)
    }

    @Test
    fun mode_returnsMostFrequent() {
        assertEquals(3.0, listOf(1.0, 2.0, 2.0, 3.0, 3.0, 3.0, 4.0).mode { it })
    }

    @Test
    fun cumsum_matchesD3() {
        assertEquals(listOf(1.0, 3.0, 6.0, 10.0, 15.0), listOf(1.0, 2.0, 3.0, 4.0, 5.0).cumsum { it })
    }
}
