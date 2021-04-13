package com.juul.krayon.kanvas

import com.juul.krayon.kanvas.Transform.InOrder
import com.juul.krayon.kanvas.Transform.Scale
import com.juul.krayon.kanvas.Transform.Skew
import com.juul.krayon.kanvas.Transform.Translate
import kotlin.test.Test
import kotlin.test.assertEquals

class TransformTests {

    @Test
    fun split_onScale() {
        val expected = InOrder(Translate(4f, 5f), Scale(2f, 3f), Translate(-4f, -5f))
        val actual = Scale(horizontal = 2f, vertical = 3f, pivotX = 4f, pivotY = 5f).split()
        assertEquals(expected, actual)
    }

    @Test
    fun split_onSkew() {
        val expected = InOrder(Skew(horizontal = 2f), Skew(vertical = 3f))
        val actual = Skew(horizontal = 2f, vertical = 3f).split()
        assertEquals(expected, actual)
    }
}
