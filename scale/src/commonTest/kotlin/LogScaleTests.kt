package com.juul.krayon.scale

import kotlin.math.log10
import kotlin.test.Test
import kotlin.test.assertEquals

class LogScaleTests {

    @Test
    fun log_mapsAsLog10OverUnitRange() {
        val scale = logScale(domain = listOf(1f, 10f), range = listOf(0f, 1f))
        assertEquals(0f, scale.scale(1f), absoluteTolerance = 1e-5f)
        assertEquals(1f, scale.scale(10f), absoluteTolerance = 1e-5f)
        assertEquals(log10(5f), scale.scale(5f), absoluteTolerance = 1e-5f)
        assertEquals(0.5f, scale.scale(sqrt10()), absoluteTolerance = 1e-5f)
    }

    @Test
    fun log_invertRoundTrips() {
        val scale = logScale(domain = listOf(1f, 10f), range = listOf(0f, 1f))
        assertEquals(sqrt10(), scale.invert(0.5f), absoluteTolerance = 1e-3f)
        assertEquals(5f, scale.invert(log10(5f)), absoluteTolerance = 1e-3f)
    }

    @Test
    fun log_ticksAcrossOneDecade() {
        val scale = logScale(domain = listOf(1f, 10f))
        assertFloatsEqual(listOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f), scale.ticks())
    }

    @Test
    fun log_ticksAcrossTwoDecades() {
        val scale = logScale(domain = listOf(1f, 100f))
        val expected = listOf(1f, 2f, 3f, 4f, 5f, 6f, 7f, 8f, 9f, 10f, 20f, 30f, 40f, 50f, 60f, 70f, 80f, 90f, 100f)
        assertFloatsEqual(expected, scale.ticks())
    }

    @Test
    fun log_niceExpandsToPowersOfBase() {
        val scale = logScale(domain = listOf(0.5f, 5f)).nice()
        assertEquals(0.1f, scale.domain.first(), absoluteTolerance = 1e-5f)
        assertEquals(10f, scale.domain.last(), absoluteTolerance = 1e-5f)
    }

    @Test
    fun log_negativeDomain() {
        val scale = logScale(domain = listOf(-100f, -1f), range = listOf(0f, 1f))
        assertEquals(0f, scale.scale(-100f), absoluteTolerance = 1e-5f)
        assertEquals(1f, scale.scale(-1f), absoluteTolerance = 1e-5f)
        // log10(10) between log10(100) and log10(1): normalized position of -10.
        assertEquals(0.5f, scale.scale(-10f), absoluteTolerance = 1e-5f)
    }

    private fun sqrt10(): Float = kotlin.math.sqrt(10f)
}
