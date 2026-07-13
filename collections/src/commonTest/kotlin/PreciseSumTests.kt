package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class PreciseSumTests {

    @Test
    fun preciseSum_isExactWhereNaiveSumIsNot() {
        assertEquals(1.0, listOf(1e-16, 1.0, -1e-16).preciseSum())
    }

    @Test
    fun preciseSum_matchesRegularSumForSimpleValues() {
        assertEquals(15.0, listOf(1.0, 2.0, 3.0, 4.0, 5.0).preciseSum())
    }

    @Test
    fun preciseSum_ignoresNaN() {
        assertEquals(6.0, listOf(1.0, Double.NaN, 2.0, 3.0).preciseSum())
    }

    @Test
    fun preciseCumulativeSums_returnsRunningTotals() {
        val result = listOf(1.0, 1e-14, -1.0, -1e-14).preciseCumulativeSums()
        assertEquals(4, result.size)
        assertEquals(1.0, result[0])
        assertEquals(0.0, result.last(), absoluteTolerance = 1e-18)
    }

    @Test
    fun adder_accumulatesExactly() {
        val adder = Adder()
        adder.add(1e-16).add(1.0).add(-1e-16)
        assertEquals(1.0, adder.value())
    }
}
