package com.juul.krayon.color

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

class ToColorTests {

    @Test
    fun toColor_withKeyword_succeeds() {
        assertEquals(cornflowerBlue, "cornflowerBlue".toColor())
    }

    @Test
    fun toColor_withRGBInput_succeeds() {
        assertEquals(red, "#f00".toColor())
        assertEquals(lime, "#0f0".toColor())
        assertEquals(blue, "#00f".toColor())
    }

    @Test
    fun toColor_withRGBAInput_succeeds() {
        assertEquals(red.copy(alpha = 0x00), "#f000".toColor())
        assertEquals(lime.copy(alpha = 0x88), "#0f08".toColor())
        assertEquals(blue.copy(alpha = 0xFF), "#00ff".toColor())
    }

    @Test
    fun toColor_withRRGGBBInput_succeeds() {
        assertEquals(cornflowerBlue, "#6495ed".toColor())
    }

    @Test
    fun toColor_withRRGGBBAAInput_succeeds() {
        assertEquals(cornflowerBlue.copy(alpha = 0x48), "#6495ed48".toColor())
    }

    @Test
    fun toColor_onMissingCrunch_throwsException() {
        assertFailsWith<IllegalArgumentException> { "fff".toColor() }
    }

    @Test
    fun toColorOrNull_onMissingCrunch_returnsNull() {
        assertNull("fff".toColorOrNull())
    }

    @Test
    fun toColor_onIncorrectLength_throwsException() {
        assertFailsWith<IllegalArgumentException> { "#fffff".toColor() }
    }

    @Test
    fun toColorOrNull_onIncorrectLength_returnsNull() {
        assertNull("#fffff".toColorOrNull())
    }

    @Test
    fun toColor_onInvalidCharacter_throwsException() {
        assertFailsWith<IllegalArgumentException> { "#fgf".toColor() }
    }

    @Test
    fun toColorOrNull_onInvalidCharacter_returnsNull() {
        assertNull("#fgf".toColorOrNull())
    }
}
