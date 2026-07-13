package com.juul.krayon.scale

import kotlin.test.Test
import kotlin.test.assertEquals

class PowScaleTests {

    @Test
    fun pow_squaresInput() {
        val scale = powScale(domain = listOf(0f, 1f), range = listOf(0f, 1f), exponent = 2f)
        assertEquals(0.25f, scale.scale(0.5f), absoluteTolerance = 1e-5f)
        assertEquals(1f, scale.scale(1f), absoluteTolerance = 1e-5f)
    }

    @Test
    fun pow_exponentOneIsLinear() {
        val scale = powScale(domain = listOf(0f, 10f), range = listOf(0f, 100f))
        assertEquals(50f, scale.scale(5f), absoluteTolerance = 1e-4f)
    }

    @Test
    fun pow_invertRoundTrips() {
        val scale = powScale(domain = listOf(0f, 1f), range = listOf(0f, 1f), exponent = 2f)
        assertEquals(0.5f, scale.invert(0.25f), absoluteTolerance = 1e-4f)
    }

    @Test
    fun pow_handlesNegativeDomainSymmetrically() {
        val scale = powScale(domain = listOf(-1f, 1f), range = listOf(-1f, 1f), exponent = 2f)
        assertEquals(0f, scale.scale(0f), absoluteTolerance = 1e-5f)
        assertEquals(-0.25f, scale.scale(-0.5f), absoluteTolerance = 1e-5f)
        assertEquals(0.25f, scale.scale(0.5f), absoluteTolerance = 1e-5f)
    }

    @Test
    fun sqrt_takesSquareRoot() {
        val scale = sqrtScale(domain = listOf(0f, 100f), range = listOf(0f, 10f))
        assertEquals(5f, scale.scale(25f), absoluteTolerance = 1e-4f)
        assertEquals(0.5f, scale.exponent, absoluteTolerance = 1e-6f)
    }

    @Test
    fun pow_ticksAreLinear() {
        val scale = powScale(domain = listOf(0f, 1f), range = listOf(0f, 1f), exponent = 2f)
        assertFloatsEqual(listOf(0f, 0.1f, 0.2f, 0.3f, 0.4f, 0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1f), scale.ticks())
    }
}
