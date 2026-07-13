package com.juul.krayon.format

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class PrecisionTests {

    @Test
    fun precisionFixed_returnsExpectedValue() {
        assertEquals(0, precisionFixed(8.9))
        assertEquals(0, precisionFixed(1.1))
        assertEquals(1, precisionFixed(0.89))
        assertEquals(1, precisionFixed(0.11))
        assertEquals(2, precisionFixed(0.089))
        assertEquals(2, precisionFixed(0.011))
    }

    @Test
    fun precisionFixed_invalidThrows() {
        assertFailsWith<IllegalArgumentException> { precisionFixed(0.0) }
        assertFailsWith<IllegalArgumentException> { precisionFixed(Double.NaN) }
        assertFailsWith<IllegalArgumentException> { precisionFixed(Double.POSITIVE_INFINITY) }
    }

    @Test
    fun precisionRound_returnsExpectedValue() {
        assertEquals(2, precisionRound(0.1, 1.1))
        assertEquals(2, precisionRound(0.01, 0.99))
        assertEquals(2, precisionRound(0.01, 1.00))
        assertEquals(3, precisionRound(0.01, 1.01))
    }

    @Test
    fun precisionRound_invalidThrows() {
        assertFailsWith<IllegalArgumentException> { precisionRound(0.0, 1.0) }
        assertFailsWith<IllegalArgumentException> { precisionRound(Double.NaN, 1.0) }
        assertFailsWith<IllegalArgumentException> { precisionRound(Double.POSITIVE_INFINITY, 1.0) }
    }

    @Test
    fun precisionPrefix_sameUnitsReturnsZero() {
        var i = -24
        while (i <= 24) {
            var j = i
            while (j < i + 3) {
                assertEquals(0, precisionPrefix(pow10(i), pow10(j)))
                j++
            }
            i += 3
        }
    }

    @Test
    fun precisionPrefix_fractionalDigitsNeeded() {
        var i = -24
        while (i <= 24) {
            var j = i - 4
            while (j < i) {
                assertEquals(i - j, precisionPrefix(pow10(j), pow10(i)))
                j++
            }
            i += 3
        }
    }

    @Test
    fun precisionPrefix_belowYocto() {
        assertEquals(0, precisionPrefix(1e-24, 1e-24))
        assertEquals(1, precisionPrefix(1e-25, 1e-25))
        assertEquals(2, precisionPrefix(1e-26, 1e-26))
        assertEquals(4, precisionPrefix(1e-28, 1e-28))
    }

    @Test
    fun precisionPrefix_aboveYotta() {
        assertEquals(0, precisionPrefix(1e24, 1e24))
        assertEquals(0, precisionPrefix(1e24, 1e27))
        assertEquals(1, precisionPrefix(1e23, 1e27))
    }

    @Test
    fun precisionPrefix_invalidThrows() {
        assertFailsWith<IllegalArgumentException> { precisionPrefix(0.0, 1.0) }
        assertFailsWith<IllegalArgumentException> { precisionPrefix(Double.NaN, 1.0) }
    }

    private fun pow10(exponent: Int): Double = "1e$exponent".toDouble()
}
