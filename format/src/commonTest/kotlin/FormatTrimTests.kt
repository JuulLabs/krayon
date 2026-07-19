package com.juul.krayon.format

import kotlin.test.Test
import kotlin.test.assertEquals

private const val MINUS = "\u2212"

class FormatTrimTests {

    @Test
    fun trim_rounded() {
        val f = format("~r")
        assertEquals("1", f(1.0))
        assertEquals("0.1", f(0.1))
        assertEquals("0.01", f(0.01))
        assertEquals("10.0001", f(10.0001))
        assertEquals("123.45", f(123.45))
        assertEquals("123.457", f(123.4567))
        assertEquals("0.000009", f(0.000009))
        assertEquals("0.111119", f(0.111119))
        assertEquals("0.111112", f(0.1111119))
        assertEquals("0.111111", f(0.11111119))
    }

    @Test
    fun trim_exponential() {
        val f = format("~e")
        assertEquals("0e+0", f(0.0))
        assertEquals("4.2e+1", f(42.0))
        assertEquals("4.2e+7", f(42000000.0))
        assertEquals("4.2e-2", f(0.042))
        assertEquals("${MINUS}4e+0", f(-4.0))
        assertEquals("4.2e+10", f(42000000000.0))
        assertEquals("4.2e-10", f(0.00000000042))
    }

    @Test
    fun trim_exponentialWithPrecision() {
        val f = format(".4~e")
        assertEquals("1.2345e-10", f(0.00000000012345))
        assertEquals("1.234e-10", f(0.00000000012340))
        assertEquals("1.23e-10", f(0.00000000012300))
        assertEquals("1.2345e+10", f(12345000000.0))
        assertEquals("1.23e+10", f(12300000000.0))
    }

    @Test
    fun trim_siPrefix() {
        val f = format("~s")
        assertEquals("0", f(0.0))
        assertEquals("100", f(100.0))
        assertEquals("999.5", f(999.5))
        assertEquals("999.5k", f(999500.0))
        assertEquals("1k", f(1000.0))
        assertEquals("1.5k", f(1500.0))
        assertEquals("1.5005k", f(1500.5))
        assertEquals("1M", f(1e6))
        assertEquals("1G", f(1e9))
        assertEquals("1P", f(1e15))
    }

    @Test
    fun trim_percent() {
        val f = format("~%")
        assertEquals("0%", f(0.0))
        assertEquals("10%", f(0.1))
        assertEquals("1%", f(0.01))
        assertEquals("0.1%", f(0.001))
        assertEquals("0.01%", f(0.0001))
    }

    @Test
    fun trim_respectsCommas() {
        val f = format(",~g")
        assertEquals("10,000", f(10000.0))
        assertEquals("10,000.1", f(10000.1))
    }

    @Test
    fun trim_clampedPrecision() {
        assertEquals("0.00000000000000000000", format(".30f")(0.0))
        assertEquals("1", format(".0g")(1.0))
    }
}
