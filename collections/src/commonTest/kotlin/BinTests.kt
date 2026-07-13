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
        expected.forEachIndexed { i, (x0, x1, values) ->
            assertEquals(x0, actual[i].x0, absoluteTolerance = 1e-9, "bin[$i].x0")
            assertEquals(x1, actual[i].x1, absoluteTolerance = 1e-9, "bin[$i].x1")
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
            bin<Double> { it }(data),
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
            bin<Double> { it }.thresholds(::thresholdScott)(data),
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
            bin<Double> { it }.thresholds(::thresholdFreedmanDiaconis)(data),
        )
    }

    @Test
    fun bin_withExplicitDomainAndThresholds_matchesD3() {
        val values = listOf(-1.0, 0.0, 5.0, 10.0, 15.0, 20.0, 25.0, 30.0, 31.0)
        assertBins(
            listOf(
                Triple(0.0, 10.0, listOf(0.0, 5.0)),
                Triple(10.0, 20.0, listOf(10.0, 15.0)),
                Triple(20.0, 30.0, listOf(20.0, 25.0, 30.0)),
            ),
            bin<Double> { it }.domain(0.0, 30.0).thresholds(listOf(0.0, 10.0, 20.0))(values),
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
            bin<Double> { it }.thresholds(5)(data),
        )
    }

    @Test
    fun thresholdStrategies_matchD3Counts() {
        assertEquals(4, thresholdSturges(data, 4.0, 42.0))
        assertEquals(2, thresholdScott(data, 4.0, 42.0))
        assertEquals(4, thresholdFreedmanDiaconis(data, 4.0, 42.0))
    }

    @Test
    fun bin_withValueAccessor_readsField() {
        data class Point(val value: Double)
        val points = listOf(Point(4.0), Point(8.0), Point(15.0), Point(16.0), Point(23.0), Point(42.0))
        val bins = bin<Point> { it.value }(points)
        assertEquals(5, bins.size)
        assertEquals(listOf(Point(4.0), Point(8.0)), bins[0].toList())
    }
}
