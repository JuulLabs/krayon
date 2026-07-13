package com.juul.krayon.transition

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EaseTests {

    private val tolerance = 1e-4f

    private val allEasings: List<Pair<String, Easing>> = listOf(
        "linear" to easeLinear,
        "quadraticIn" to easeQuadraticIn,
        "quadraticOut" to easeQuadraticOut,
        "quadraticInOut" to easeQuadraticInOut,
        "cubicIn" to easeCubicIn,
        "cubicOut" to easeCubicOut,
        "cubicInOut" to easeCubicInOut,
        "cubic" to easeCubic,
        "sinusoidalIn" to easeSinusoidalIn,
        "sinusoidalOut" to easeSinusoidalOut,
        "sinusoidalInOut" to easeSinusoidalInOut,
        "exponentialIn" to easeExponentialIn,
        "exponentialOut" to easeExponentialOut,
        "exponentialInOut" to easeExponentialInOut,
        "circularIn" to easeCircularIn,
        "circularOut" to easeCircularOut,
        "circularInOut" to easeCircularInOut,
        "polynomialIn" to easePolynomialIn(),
        "polynomialOut" to easePolynomialOut(),
        "polynomialInOut" to easePolynomialInOut(),
        "elasticIn" to easeElasticIn(),
        "elasticOut" to easeElasticOut(),
        "elasticInOut" to easeElasticInOut(),
        "backIn" to easeBackIn(),
        "backOut" to easeBackOut(),
        "backInOut" to easeBackInOut(),
        "bounceIn" to easeBounceIn,
        "bounceOut" to easeBounceOut,
        "bounceInOut" to easeBounceInOut,
    )

    @Test
    fun allEasings_pinEndpoints() {
        for ((name, easing) in allEasings) {
            assertEquals(0f, easing.ease(0f), tolerance, "$name should ease(0) == 0")
            assertEquals(1f, easing.ease(1f), tolerance, "$name should ease(1) == 1")
        }
    }

    @Test
    fun easeLinear_isIdentity() {
        for (i in 0..10) {
            val t = i / 10f
            assertEquals(t, easeLinear.ease(t), tolerance)
        }
    }

    @Test
    fun polynomialFamilies_matchKnownValues() {
        assertEquals(0.25f, easeQuadraticIn.ease(0.5f), tolerance)
        assertEquals(0.75f, easeQuadraticOut.ease(0.5f), tolerance)
        assertEquals(0.5f, easeQuadraticInOut.ease(0.5f), tolerance)
        assertEquals(0.125f, easeCubicIn.ease(0.5f), tolerance)
        assertEquals(0.875f, easeCubicOut.ease(0.5f), tolerance)
        assertEquals(0.5f, easeCubicInOut.ease(0.5f), tolerance)
        // Polynomial defaults to exponent 3, so it must match cubic.
        assertEquals(easeCubicIn.ease(0.3f), easePolynomialIn().ease(0.3f), tolerance)
        assertEquals(easeCubicOut.ease(0.7f), easePolynomialOut().ease(0.7f), tolerance)
        // Exponent 2 must match quadratic.
        assertEquals(easeQuadraticIn.ease(0.4f), easePolynomialIn(2f).ease(0.4f), tolerance)
    }

    @Test
    fun symmetricInOut_easingsAreSymmetric() {
        val symmetric = listOf(
            "quadraticInOut" to easeQuadraticInOut,
            "cubicInOut" to easeCubicInOut,
            "sinusoidalInOut" to easeSinusoidalInOut,
            "exponentialInOut" to easeExponentialInOut,
            "circularInOut" to easeCircularInOut,
            "polynomialInOut" to easePolynomialInOut(),
            "bounceInOut" to easeBounceInOut,
        )
        for ((name, easing) in symmetric) {
            for (i in 0..10) {
                val t = i / 10f
                assertEquals(1f, easing.ease(t) + easing.ease(1f - t), 1e-3f, "$name not symmetric at t=$t")
            }
            assertEquals(0.5f, easing.ease(0.5f), 1e-3f, "$name midpoint should be 0.5")
        }
    }

    @Test
    fun backAndElastic_overshoot() {
        // Back easing-in dips below zero before returning.
        assertTrue(easeBackIn().ease(0.3f) < 0f, "backIn should undershoot")
        // Back easing-out overshoots above one before settling.
        assertTrue(easeBackOut().ease(0.7f) > 1f, "backOut should overshoot")
        // Elastic oscillates; out easing exceeds 1 somewhere in the middle.
        val elasticOut = easeElasticOut()
        assertTrue((1..9).any { elasticOut.ease(it / 10f) > 1f }, "elasticOut should overshoot")
    }

    @Test
    fun bounceOut_matchesKnownValues() {
        // From d3-ease reference: bounceOut(0.5) == 0.765625.
        assertEquals(0.765625f, easeBounceOut.ease(0.5f), tolerance)
        assertEquals(0f, easeBounceOut.ease(0f), tolerance)
        assertEquals(1f, easeBounceOut.ease(1f), tolerance)
    }
}
