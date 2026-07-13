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
    fun bisector_withAccessor_bisectsByKey() {
        data class Point(val x: Int)
        val points = listOf(Point(1), Point(3), Point(3), Point(8))
        val bisector = bisector<Point, Int> { it.x }
        assertEquals(1, bisector.left(points, 3))
        assertEquals(3, bisector.right(points, 3))
    }

    @Test
    fun bisector_withComparator_bisectsElements() {
        val bisector = bisector<Int>(naturalOrder())
        assertEquals(2, bisector.left(values, 3))
        assertEquals(5, bisector.right(values, 3))
    }
}
