package com.juul.krayon.color

import kotlin.test.Test
import kotlin.test.assertEquals

class OperationsTests {

    @Test
    fun lerp_fromWhiteToBlack_isGray() {
        assertEquals(gray, lerp(white, black, 0.5f))
    }

    @Test
    fun brighter_steelBlue_matchesD3() {
        assertEquals("#64baff".toColor(), steelBlue.brighter())
    }

    @Test
    fun darker_steelBlue_matchesD3() {
        assertEquals("#315b7e".toColor(), steelBlue.darker())
    }
}
