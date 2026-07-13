package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class FsumTests {

    @Test
    fun fsum_isExactWhereNaiveSumIsNot() {
        val values = listOf(1e-16, 1.0, -1e-16)
        assertEquals(1.0, values.fsum { it })
    }

    @Test
    fun fsum_matchesRegularSumForSimpleValues() {
        assertEquals(15.0, listOf(1.0, 2.0, 3.0, 4.0, 5.0).fsum { it })
    }

    @Test
    fun fsum_ignoresNaN() {
        assertEquals(6.0, listOf(1.0, Double.NaN, 2.0, 3.0).fsum { it })
    }

    @Test
    fun fcumsum_returnsRunningTotals() {
        val result = listOf(1.0, 1e-14, -1.0, -1e-14).fcumsum { it }
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
