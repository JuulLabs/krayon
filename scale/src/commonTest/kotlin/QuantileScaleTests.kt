package com.juul.krayon.scale

import kotlin.test.Test
import kotlin.test.assertEquals

class QuantileScaleTests {

    private fun scale() = quantileScale(
        domain = listOf(0f, 5f, 7f, 10f, 20f, 30f),
        range = listOf(0, 1, 2, 3),
    )

    @Test
    fun quantile_computesR7Quantiles() {
        assertFloatsEqual(listOf(5.5f, 8.5f, 17.5f), scale().quantiles)
    }

    @Test
    fun quantile_mapsBySampleQuantiles() {
        val scale = scale()
        assertEquals(0, scale.scale(3f))
        assertEquals(1, scale.scale(6f))
        assertEquals(2, scale.scale(9f))
        assertEquals(3, scale.scale(20f))
    }

    @Test
    fun quantile_sortsUnsortedDomain() {
        val scale = quantileScale(domain = listOf(30f, 0f, 10f, 5f, 20f, 7f), range = listOf(0, 1, 2, 3))
        assertFloatsEqual(listOf(5.5f, 8.5f, 17.5f), scale.quantiles)
    }

    @Test
    fun quantile_invertExtent() {
        val extent = scale().invertExtent(2)
        assertEquals(8.5f, extent.first, absoluteTolerance = 1e-4f)
        assertEquals(17.5f, extent.second, absoluteTolerance = 1e-4f)
    }
}
