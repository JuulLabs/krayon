package com.juul.krayon.shape

import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

class StackTest {

    private val data = listOf(
        listOf(1f, 3f, 5f, 1f),
        listOf(2f, 4f, 2f, 3f),
        listOf(1f, 2f, 4f, 2f),
    )

    private fun assertSeries(
        series: Series<*, *>,
        vararg expected: Pair<Float, Float>,
        tolerance: Float = 1e-4f,
    ) {
        assertEquals(expected.size, series.size, "point count for series ${series.key}")
        for (i in expected.indices) {
            assertEquals(expected[i].first, series[i].low, tolerance, "low[$i] of series ${series.key}")
            assertEquals(expected[i].second, series[i].high, tolerance, "high[$i] of series ${series.key}")
        }
    }

    @Test
    fun stack_computesStackedSeriesForData() {
        val result = stack<List<Float>, Int>().keys(listOf(0, 1, 2, 3)).invoke(data)
        assertSeries(result[0], 0f to 1f, 0f to 2f, 0f to 1f)
        assertSeries(result[1], 1f to 4f, 2f to 6f, 1f to 3f)
        assertSeries(result[2], 4f to 9f, 6f to 8f, 3f to 7f)
        assertSeries(result[3], 9f to 10f, 8f to 11f, 7f to 9f)
        assertContentEquals(listOf(0, 1, 2, 3), result.map { it.index })
    }

    @Test
    fun stack_withMapData_usesDefaultValueAccessor() {
        val mapData = listOf(mapOf("a" to 1f, "b" to 3f), mapOf("a" to 2f, "b" to 4f))
        val result = stack<Map<String, Float>, String>().keys(listOf("a", "b")).invoke(mapData)
        assertSeries(result[0], 0f to 1f, 0f to 2f)
        assertSeries(result[1], 1f to 4f, 2f to 6f)
        assertEquals("a", result[0].key)
        assertEquals("b", result[1].key)
    }

    @Test
    fun stack_withCustomValueAccessor_passesDatumAndKey() {
        val result = stack<List<Float>, Int>()
            .keys(listOf(0, 1))
            .value { datum, key -> datum[key] * 2 }
            .invoke(listOf(listOf(1f, 3f)))
        assertSeries(result[0], 0f to 2f)
        assertSeries(result[1], 2f to 8f)
    }

    @Test
    fun stack_orderReverse_reversesStackingOrder() {
        val result = stack<List<Float>, Int>().keys(listOf(0, 1, 2, 3)).order(StackOrderReverse).invoke(data)
        assertSeries(result[0], 9f to 10f, 9f to 11f, 8f to 9f)
        assertSeries(result[1], 6f to 9f, 5f to 9f, 6f to 8f)
        assertSeries(result[2], 1f to 6f, 3f to 5f, 2f to 6f)
        assertSeries(result[3], 0f to 1f, 0f to 3f, 0f to 2f)
        assertContentEquals(listOf(3, 2, 1, 0), result.map { it.index })
    }

    @Test
    fun stack_offsetExpand_normalizesColumns() {
        val result = stack<List<Float>, Int>().keys(listOf(0, 1, 2, 3)).offset(StackOffsetExpand).invoke(data)
        assertSeries(result[0], 0f / 10 to 1f / 10, 0f / 11 to 2f / 11, 0f / 9 to 1f / 9)
        assertSeries(result[3], 9f / 10 to 10f / 10, 8f / 11 to 11f / 11, 7f / 9 to 9f / 9)
    }

    @Test
    fun stackOrderAscending_ordersBySum() {
        assertContentEquals(intArrayOf(2, 0, 1).toList(), StackOrderAscending(orderFixture()).toList())
    }

    @Test
    fun stackOrderDescending_ordersBySum() {
        assertContentEquals(intArrayOf(1, 0, 2).toList(), StackOrderDescending(orderFixture()).toList())
    }

    @Test
    fun stackOrderAppearance_ordersByPeak() {
        val series = seriesOf(
            listOf(0f, 0f, 1f),
            listOf(3f, 2f, 0f),
            listOf(0f, 4f, 0f),
        )
        assertContentEquals(intArrayOf(1, 2, 0).toList(), StackOrderAppearance(series).toList())
    }

    @Test
    fun stackOrderInsideOut_ordersByAppearanceInsideOut() {
        val series = seriesOf(
            listOf(0f, 0f, 0f, 0f, 0f, 0f, 1f),
            listOf(0f, 0f, 0f, 0f, 0f, 2f, 0f),
            listOf(0f, 0f, 0f, 0f, 3f, 0f, 0f),
            listOf(0f, 0f, 0f, 4f, 0f, 0f, 0f),
            listOf(0f, 0f, 5f, 0f, 0f, 0f, 0f),
            listOf(0f, 6f, 0f, 0f, 0f, 0f, 0f),
            listOf(7f, 0f, 0f, 0f, 0f, 0f, 0f),
        )
        assertContentEquals(intArrayOf(2, 3, 6, 5, 4, 1, 0).toList(), StackOrderInsideOut(series).toList())
    }

    @Test
    fun stackOffsetSilhouette_centersAroundZero() {
        val result = stack<List<Float>, Int>().keys(listOf(0, 1, 2)).offset(StackOffsetSilhouette).invoke(data)
        assertSeries(result[0], 0f - 9f / 2 to 1f - 9f / 2, 0f - 8f / 2 to 2f - 8f / 2, 0f - 7f / 2 to 1f - 7f / 2)
        assertSeries(result[2], 4f - 9f / 2 to 9f - 9f / 2, 6f - 8f / 2 to 8f - 8f / 2, 3f - 7f / 2 to 7f - 7f / 2)
    }

    @Test
    fun stackOffsetWiggle_minimizesWeightedWiggle() {
        val result = stack<List<Float>, Int>().keys(listOf(0, 1, 2)).offset(StackOffsetWiggle).invoke(data)
        assertSeries(result[0], 0f to 1f, -1f to 1f, 0.7857143f to 1.7857143f)
        assertSeries(result[1], 1f to 4f, 1f to 5f, 1.7857143f to 3.7857143f)
        assertSeries(result[2], 4f to 9f, 5f to 7f, 3.7857143f to 7.7857143f)
    }

    private fun orderFixture(): List<Series<Unit, Int>> = seriesOf(
        listOf(1f, 2f, 3f),
        listOf(2f, 3f, 4f),
        listOf(0f, 1f, 2f),
    )

    private fun seriesOf(vararg rows: List<Float>): List<Series<Unit, Int>> =
        rows.mapIndexed { index, row ->
            Series(index, row.map { StackPoint(Unit, low = 0f, high = it) })
        }
}
