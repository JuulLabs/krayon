package com.juul.krayon.scale

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ContinuousScaleFeatureTests {

    @Test
    fun invert_isInverseOfScaleForBimap() {
        val scale = scale(domain = listOf(0f, 10f), range = listOf(0f, 100f))
        assertEquals(5f, scale.invert(50f), absoluteTolerance = 1e-4f)
        assertEquals(0f, scale.invert(0f), absoluteTolerance = 1e-4f)
        assertEquals(10f, scale.invert(100f), absoluteTolerance = 1e-4f)
    }

    @Test
    fun invert_handlesPiecewiseRange() {
        val scale = scale(domain = listOf(0f, 10f, 100f), range = listOf(0f, 50f, 100f))
        assertEquals(55f, scale.invert(75f), absoluteTolerance = 1e-3f)
        assertEquals(5f, scale.invert(25f), absoluteTolerance = 1e-3f)
    }

    @Test
    fun clamp_coercesInputsToDomainBounds() {
        val scale = scale(domain = listOf(0f, 10f), range = listOf(0f, 100f)).clamp()
        assertEquals(100f, scale.scale(20f), absoluteTolerance = 1e-4f)
        assertEquals(0f, scale.scale(-5f), absoluteTolerance = 1e-4f)
        assertEquals(true, scale.clamp)
    }

    @Test
    fun clamp_coercesInvertOutputs() {
        val scale = scale(domain = listOf(0f, 10f), range = listOf(0f, 100f)).clamp()
        assertEquals(10f, scale.invert(150f), absoluteTolerance = 1e-4f)
        assertEquals(0f, scale.invert(-50f), absoluteTolerance = 1e-4f)
    }

    @Test
    fun unclampedScale_throwsOutsideDomain() {
        val scale = scale(domain = listOf(0f, 10f), range = listOf(0f, 100f))
        assertFailsWith<IllegalStateException> { scale.scale(20f) }
    }

    @Test
    fun ticks_spanDomain() {
        val scale = scale(domain = listOf(0f, 10f), range = listOf(0f, 1f))
        assertFloatsEqual(listOf(0f, 1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f), scale.ticks())
    }

    @Test
    fun nice_extendsDomainToRoundValues() {
        val scale = scale(domain = listOf(0.1f, 9.9f), range = listOf(0f, 1f)).nice()
        assertEquals(0f, scale.domain.first(), absoluteTolerance = 1e-4f)
        assertEquals(10f, scale.domain.last(), absoluteTolerance = 1e-4f)
    }

    @Test
    fun tickFormat_usesStepPrecision() {
        val format = scale(domain = listOf(0f, 1f), range = listOf(0f, 1f)).tickFormat()
        assertEquals("0.5", format(0.5f))
        assertEquals("0.0", format(0f))
        assertEquals("1.0", format(1f))
    }
}
