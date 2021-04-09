package com.juul.krayon.color

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class RandomTests {

    private val saturatedRandom = object : Random() {
        override fun nextBits(bitCount: Int): Int = 0xFFFFFFFF.toInt() ushr (32 - bitCount)
    }

    private val zeroRandom = object : Random() {
        override fun nextBits(bitCount: Int): Int = 0
    }

    @Test
    fun nextColor_withSaturatedRandom_isWhite() {
        assertEquals(white.rgb, saturatedRandom.nextColor().rgb)
    }

    @Test
    fun nextColor_withDefaultArg_isOpaque() {
        assertEquals(0xFF, zeroRandom.nextColor().alpha)
    }

    @Test
    fun nextColor_withOpaqueFalse_canProduceTransparency() {
        assertEquals(0, zeroRandom.nextColor(isOpaque = false).alpha)
    }
}
