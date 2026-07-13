package com.juul.krayon.scale

import kotlin.test.Test
import kotlin.test.assertEquals

class QuantizeScaleTests {

    @Test
    fun quantize_partitionsDomainUniformly() {
        val scale = quantizeScale(domain = listOf(0f, 100f), range = listOf("a", "b", "c", "d"))
        assertFloatsEqual(listOf(25f, 50f, 75f), scale.thresholds)
        assertEquals("a", scale.scale(10f))
        assertEquals("b", scale.scale(30f))
        assertEquals("c", scale.scale(60f))
        assertEquals("d", scale.scale(90f))
    }

    @Test
    fun quantize_boundaryGoesToUpperSegment() {
        val scale = quantizeScale(domain = listOf(0f, 100f), range = listOf("a", "b", "c", "d"))
        assertEquals("b", scale.scale(25f))
    }

    @Test
    fun quantize_clampsOutsideDomain() {
        val scale = quantizeScale(domain = listOf(0f, 100f), range = listOf("a", "b", "c", "d"))
        assertEquals("a", scale.scale(-50f))
        assertEquals("d", scale.scale(200f))
    }

    @Test
    fun quantize_invertExtent() {
        val scale = quantizeScale(domain = listOf(0f, 100f), range = listOf("a", "b", "c", "d"))
        assertEquals(25f, scale.invertExtent("b").first, absoluteTolerance = 1e-4f)
        assertEquals(50f, scale.invertExtent("b").second, absoluteTolerance = 1e-4f)
    }
}
