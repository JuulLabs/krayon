package com.juul.krayon.format

import kotlin.test.Test
import kotlin.test.assertEquals

private const val MINUS = "\u2212"
private const val MICRO = "\u00b5"

class FormatPrefixTests {

    @Test
    fun formatPrefix_usesPrefixAppropriateToValue() {
        assertEquals("420$MICRO", formatPrefix(",.0s", 1e-6)(0.00042))
        assertEquals("4,200$MICRO", formatPrefix(",.0s", 1e-6)(0.0042))
        assertEquals("0.420m", formatPrefix(",.3s", 1e-3)(0.00042))
    }

    @Test
    fun formatPrefix_extremeReferenceValues() {
        assertEquals("1y", formatPrefix(",.0s", 1e-27)(1e-24))
        assertEquals("1Y", formatPrefix(",.0s", 1e27)(1e24))
    }

    @Test
    fun formatPrefix_currencyAndWidth() {
        val f = formatPrefix(" $12,.1s", 1e6)
        assertEquals(" ".repeat(5) + "$MINUS$42.0M", f(-42e6))
        assertEquals(" ".repeat(7) + "$4.2M", f(4.2e6))
    }

    @Test
    fun formatPrefix_parentheses() {
        assertEquals("$1k", formatPrefix("($~s", 1e3)(1e3))
        assertEquals("($1k)", formatPrefix("($~s", 1e3)(-1e3))
    }
}
