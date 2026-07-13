package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class QuickselectTests {

    @Test
    fun quickselect_placesKthSmallestAtIndexK() {
        val list = mutableListOf(9, 1, 8, 2, 7, 3, 6, 4, 5, 0)
        list.quickselect(k = 4, comparator = naturalOrder())
        assertEquals(4, list[4])
        for (i in 0 until 4) require(list[i] <= list[4])
        for (i in 5 until list.size) require(list[i] >= list[4])
    }

    @Test
    fun quickselect_findsMedianElement() {
        val list = mutableListOf(5.0, 1.0, 3.0, 2.0, 4.0)
        list.quickselect(k = 2, comparator = naturalOrder())
        assertEquals(3.0, list[2])
    }
}
