package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class BinTests {

    private val data = listOf(4.0, 8.0, 15.0, 16.0, 23.0, 42.0)

    private fun assertBins(
        expected: List<Triple<Double, Double, List<Double>>>,
        actual: List<Bin<Double>>,
    ) {
        assertEquals(expected.size, actual.size, "bin count")
        expected.forEachIndexed { i, (lower, upper, values) ->
            assertEquals(lower, actual[i].lowerBound, absoluteTolerance = 1e-9, "bin[$i].lowerBound")
            assertEquals(upper, actual[i].upperBound, absoluteTolerance = 1e-9, "bin[$i].upperBound")
            assertEquals(values, actual[i].toList(), "bin[$i] values")
        }
    }

    @Test
    fun bin_withDefaultSturgesThresholds_matchesD3() {
        assertBins(
            listOf(
                Triple(0.0, 10.0, listOf(4.0, 8.0)),
                Triple(10.0, 20.0, listOf(15.0, 16.0)),
                Triple(20.0, 30.0, listOf(23.0)),
                Triple(30.0, 40.0, emptyList()),
                Triple(40.0, 50.0, listOf(42.0)),
            ),
            data.bin { it },
        )
    }

    @Test
    fun bin_withScottThresholds_matchesD3() {
        assertBins(
            listOf(
                Triple(0.0, 20.0, listOf(4.0, 8.0, 15.0, 16.0)),
                Triple(20.0, 40.0, listOf(23.0)),
                Triple(40.0, 60.0, listOf(42.0)),
            ),
            data.bin(thresholds = Thresholds.Scott) { it },
        )
    }

    @Test
    fun bin_withFreedmanDiaconisThresholds_matchesD3() {
        assertBins(
            listOf(
                Triple(0.0, 10.0, listOf(4.0, 8.0)),
                Triple(10.0, 20.0, listOf(15.0, 16.0)),
                Triple(20.0, 30.0, listOf(23.0)),
                Triple(30.0, 40.0, emptyList()),
                Triple(40.0, 50.0, listOf(42.0)),
            ),
            data.bin(thresholds = Thresholds.FreedmanDiaconis) { it },
        )
    }

    @Test
    fun bin_withCountThreshold_matchesD3() {
        assertBins(
            listOf(
                Triple(0.0, 10.0, listOf(4.0, 8.0)),
                Triple(10.0, 20.0, listOf(15.0, 16.0)),
                Triple(20.0, 30.0, listOf(23.0)),
                Triple(30.0, 40.0, emptyList()),
                Triple(40.0, 50.0, listOf(42.0)),
            ),
            data.bin(thresholds = Thresholds.Count(5)) { it },
        )
    }

    @Test
    fun bin_withExplicitBoundariesAndDomain_matchesD3() {
        val values = listOf(-1.0, 0.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 31.0)
        assertBins(
            listOf(
                Triple(0.0, 10.0, listOf(0.0, 5.0)),
                Triple(10.0, 20.0, listOf(10.0, 15.0)),
                Triple(20.0, 30.0, listOf(20.0, 25.0, 30.0)),
            ),
            values.bin(domain = { 0.0..30.0 }, thresholds = Thresholds.Boundaries(listOf(0.0, 10.0, 20.0))) { it },
        )
    }

    @Test
    fun bin_withValueAccessor_readsField() {
        val points = listOf(Point(4.0), Point(8.0), Point(15.0), Point(16.0), Point(23.0), Point(42.0))
        val bins = points.bin { it.value }
        assertEquals(5, bins.size)
        assertEquals(listOf(Point(4.0), Point(8.0)), bins[0].toList())
    }

    private data class Point(val value: Double)
}
