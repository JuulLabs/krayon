package com.juul.krayon.color

import kotlin.test.Test
import kotlin.test.assertEquals

class OperationsTests {

    @Test
    fun lerp_fromWhiteToBlack_isGray() {
        assertEquals(gray, lerp(white, black, 0.5f))
    }
}
