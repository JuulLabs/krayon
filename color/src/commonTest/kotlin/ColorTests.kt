package com.juul.krayon.color

import kotlin.test.Test
import kotlin.test.assertEquals

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
}
