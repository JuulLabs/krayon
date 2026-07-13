package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class QuantileTests {

    private val values = listOf(0.0, 10.0, 30.0)

    @Test
    fun quantile_atBoundaries_returnsExtremes() {
        assertEquals(0.0, values.quantile(0.0) { it })
        assertEquals(30.0, values.quantile(1.0) { it })
    }

    @Test
    fun quantile_interpolatesBetweenValues() {
        assertEquals(5.0, values.quantile(0.25) { it })
        assertEquals(10.0, values.quantile(0.5) { it })
        assertEquals(20.0, values.quantile(0.75) { it })
    }

    @Test
    fun quantile_matchesD3ForLargerSample() {
        val sample = listOf(6.0, 3.0, 7.0, 8.0, 8.0, 10.0, 13.0, 15.0, 16.0, 20.0)
        assertEquals(7.25, sample.quantile(0.25) { it })
        assertEquals(9.0, sample.quantile(0.5) { it })
        assertEquals(14.5, sample.quantile(0.75) { it })
    }

    @Test
    fun quantile_ofEmpty_isNull() {
        assertNull(emptyList<Double>().quantile(0.5) { it })
    }

    @Test
    fun quantile_ofSingleValue_returnsThatValue() {
        assertEquals(42.0, listOf(42.0).quantile(0.5) { it })
    }

    @Test
    fun quantile_sortsInput() {
        assertEquals(10.0, listOf(30.0, 0.0, 10.0).quantile(0.5) { it })
    }

    @Test
    fun quantileSorted_matchesQuantile() {
        assertEquals(20.0, values.quantileSorted(0.75) { it })
    }

    @Test
    fun median_isHalfQuantile() {
        assertEquals(3.0, listOf(1.0, 2.0, 3.0, 4.0, 5.0).median { it })
    }
}
