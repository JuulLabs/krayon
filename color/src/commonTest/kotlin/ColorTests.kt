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

    /** This test is weird, but code coverage requires I do something with the builtins. */
    @Test
    fun checkBuiltInColors() {
        fun assertColorEquals(alpha: Int, red: Int, green: Int, blue: Int, actual: Color) {
            require(alpha in 0..0xFF && red in 0..0xFF && green in 0..0xFF && blue in 0..0xFF) { "You wrote the test wrong." }
            assertEquals(Color(alpha, red, green, blue), actual)
            assertEquals(alpha, actual.alpha)
            assertEquals(red, actual.red)
            assertEquals(green, actual.green)
            assertEquals(blue, actual.blue)
        }
        assertColorEquals(alpha = 0x00, red = 0x00, green = 0x00, blue = 0x00, Color.transparent)
        assertColorEquals(alpha = 0xFF, red = 0x00, green = 0x00, blue = 0x00, Color.black)
        assertColorEquals(alpha = 0xFF, red = 0xFF, green = 0xFF, blue = 0xFF, Color.white)
        assertColorEquals(alpha = 0xFF, red = 0xFF, green = 0x00, blue = 0x00, Color.red)
        assertColorEquals(alpha = 0xFF, red = 0x00, green = 0xFF, blue = 0x00, Color.green)
        assertColorEquals(alpha = 0xFF, red = 0x00, green = 0x00, blue = 0xFF, Color.blue)
        assertColorEquals(alpha = 0xFF, red = 0x00, green = 0xFF, blue = 0xFF, Color.cyan)
        assertColorEquals(alpha = 0xFF, red = 0xFF, green = 0x00, blue = 0xFF, Color.magenta)
        assertColorEquals(alpha = 0xFF, red = 0xFF, green = 0xFF, blue = 0x00, Color.yellow)
    }
}
