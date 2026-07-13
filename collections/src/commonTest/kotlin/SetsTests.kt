package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SetsTests {

    @Test
    fun union_combinesUniqueValuesInOrder() {
        assertEquals(setOf(1, 2, 3, 4), union(listOf(1, 2), listOf(2, 3, 4)))
        assertEquals(listOf(1, 2, 3, 4), union(listOf(1, 2), listOf(2, 3, 4)).toList())
    }

    @Test
    fun intersection_keepsCommonValues() {
        assertEquals(setOf(2, 3), intersection(listOf(1, 2, 3), listOf(2, 3, 4)))
    }

    @Test
    fun difference_removesOthers() {
        assertEquals(setOf(1, 3), difference(listOf(1, 2, 3, 4), listOf(2, 4)))
    }

    @Test
    fun superset_and_subset() {
        assertTrue(superset(listOf(1, 2, 3, 4), listOf(2, 3)))
        assertFalse(superset(listOf(1, 2), listOf(2, 3)))
        assertTrue(subset(listOf(2, 3), listOf(1, 2, 3)))
    }

    @Test
    fun disjoint_detectsNoOverlap() {
        assertTrue(disjoint(listOf(1, 2), listOf(3, 4)))
        assertFalse(disjoint(listOf(1, 2), listOf(2, 3)))
    }
}
