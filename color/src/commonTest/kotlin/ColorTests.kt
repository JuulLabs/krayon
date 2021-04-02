package com.juul.krayon.color

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class ColorTests {

    @Test
    fun checkArgbTransformIsCorrect() {
        assertEquals((0x12345678).toInt(), Color(alpha = 0x12, red = 0x34, green = 0x56, blue = 0x78).argb)
    }

    @Test
    fun checkComponentRetrievalIsCorrect() {
        val color = Color(0x12345678)
        assertEquals(0x12, color.alpha)
        assertEquals(0x34, color.red)
        assertEquals(0x56, color.green)
        assertEquals(0x78, color.blue)
    }

    @Test
    fun checkCopyWorks() {
        val expected = Color(0x12345678)
        assertEquals(expected, expected.copy())
    }

    @Test
    fun checkComponentsAreMasked() {
        assertEquals((0x23456789).toInt(), Color(alpha = 0x123, red = 0x345, green = 0x567, blue = 0x789).argb)
    }

    @Test
    fun colorConstructor_withFloatInput_matchesIntegerInput() {
        assertEquals(red, Color(red = 1f, green = 0f, blue = 0f))
    }

    @Test
    fun colorConstructor_withNegativeFloatInput_throwsException() {
        assertFailsWith<IllegalArgumentException> { Color(red = -1f, green = 0f, blue = 0f) }
    }

    @Test
    fun colorConstructor_withLargeFloatInput_throwsException() {
        assertFailsWith<IllegalArgumentException> { Color(red = 2f, green = 0f, blue = 0f) }
    }

    @Test
    fun toHexString_withNoTransparency_omitsAlpha() {
        assertEquals("#ff0000", red.toHexString())
    }

    @Test
    fun toHexString_withTransparency_includesAlpha() {
        assertEquals("#ff000080", red.copy(alpha = 0x80).toHexString())
    }
}
