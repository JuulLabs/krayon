package com.juul.krayon.kanvas.svg

import kotlin.math.PI
import kotlin.test.Test
import kotlin.test.assertEquals

class ScientificTests {

    @Test
    fun scientificNotation_forZero_isSimple() {
        assertEquals("0", 0.0.scientificNotation(6))
    }

    @Test
    fun scientificNotation_forSmallIntegers_omitsDecimalAndE() {
        assertEquals("7", 7.0.scientificNotation(6))
    }
    @Test
    fun scientificNotation_forSmallDecimals_omitsE() {
        assertEquals("7", 7.0.scientificNotation(6))
    }

    @Test
    fun scientificNotation_forSmallValueWithPrecision1_omitsDecimalAndE() {
        assertEquals("7", 7.5.scientificNotation(1))
    }

    @Test
    fun scientificNotation_forLargeValuesWithPrecision1_omitsDecimal() {
        assertEquals("1e1", 12.3.scientificNotation(1))
    }

    @Test
    fun scientificNotation_forPrecisionCutOff_canRoundUp() {
        assertEquals("3.142", PI.scientificNotation(4))
    }

    @Test
    fun scientificNotation_forPrecisionCutOff_canRoundDown() {
        assertEquals("3.14", PI.scientificNotation(3))
    }

    @Test
    fun scientificNotation_forLargeIntegers_includesAllComponents() {
        assertEquals("1.23e2", 123.0.scientificNotation(5))
    }

    @Test
    fun scientificNotation_forTinyValues_includesNegativeExponent() {
        assertEquals("1.23e-1", 0.123.scientificNotation(5))
    }
    @Test
    fun scientificNotation_forNegativeValues_includesNegativeSign() {
        assertEquals("-1.23e-1", (-0.123).scientificNotation(5))
    }
}
