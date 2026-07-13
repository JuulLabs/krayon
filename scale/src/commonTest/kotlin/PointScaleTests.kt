package com.juul.krayon.scale

import kotlin.test.Test
import kotlin.test.assertEquals

class PointScaleTests {

    @Test
    fun point_withoutPadding_placesPointsAtBandStarts() {
        val scale = pointScale(domain = listOf("a", "b", "c", "d"), range = listOf(0f, 120f))
        assertEquals(0f, scale.scale("a"), absoluteTolerance = 1e-4f)
        assertEquals(40f, scale.scale("b"), absoluteTolerance = 1e-4f)
        assertEquals(80f, scale.scale("c"), absoluteTolerance = 1e-4f)
        assertEquals(120f, scale.scale("d"), absoluteTolerance = 1e-4f)
        assertEquals(40f, scale.step, absoluteTolerance = 1e-4f)
        assertEquals(0f, scale.bandwidth, absoluteTolerance = 1e-4f)
    }

    @Test
    fun point_withPadding_matchesD3() {
        // d3: scalePoint().domain(["a","b","c","d"]).range([0,100]).padding(0.5)
        val scale = pointScale(domain = listOf("a", "b", "c", "d"), range = listOf(0f, 100f)).padding(0.5f)
        assertEquals(12.5f, scale.scale("a"), absoluteTolerance = 1e-3f)
        assertEquals(37.5f, scale.scale("b"), absoluteTolerance = 1e-3f)
        assertEquals(62.5f, scale.scale("c"), absoluteTolerance = 1e-3f)
        assertEquals(87.5f, scale.scale("d"), absoluteTolerance = 1e-3f)
        assertEquals(25f, scale.step, absoluteTolerance = 1e-3f)
    }

    @Test
    fun point_singleValue_centersInRange() {
        val scale = pointScale(domain = listOf("only"), range = listOf(0f, 100f))
        assertEquals(50f, scale.scale("only"), absoluteTolerance = 1e-4f)
    }
}
