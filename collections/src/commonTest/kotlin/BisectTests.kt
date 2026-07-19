package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class BisectTests {

    private val values = listOf(1, 2, 3, 3, 3, 5, 8)

    @Test
    fun bisectLeft_findsLeftmostInsertionPoint() {
        assertEquals(2, values.bisectLeft(3))
    }

    @Test
    fun bisectRight_findsRightmostInsertionPoint() {
        assertEquals(5, values.bisectRight(3))
    }

    @Test
    fun bisect_isAliasForBisectRight() {
        assertEquals(values.bisectRight(3), values.bisect(3))
    }

    @Test
    fun bisect_forAbsentValue_returnsInsertionPoint() {
        assertEquals(5, values.bisectLeft(4))
    }

    @Test
    fun bisect_atBoundaries() {
        assertEquals(0, values.bisectLeft(0))
        assertEquals(7, values.bisectRight(9))
    }

    @Test
    fun bisect_withRange_respectsBounds() {
        assertEquals(2, values.bisectLeft(3, from = 1, to = 4))
    }

    @Test
    fun bisectLeft_withSelector_bisectsByKey() {
        val points = listOf(Point(1), Point(3), Point(3), Point(8))
        assertEquals(1, points.bisectLeft(3) { it.x })
    }

    @Test
    fun bisectRight_withSelector_bisectsByKey() {
        val points = listOf(Point(1), Point(3), Point(3), Point(8))
        assertEquals(3, points.bisectRight(3) { it.x })
    }

    private data class Point(val x: Int)
}
