package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class TransformationsTests {

    @Test
    fun cross_producesCartesianProduct() {
        assertEquals(
            listOf(1 to "a", 1 to "b", 2 to "a", 2 to "b"),
            listOf(1, 2) cross listOf("a", "b"),
        )
    }

    @Test
    fun cross_withEmpty_isEmpty() {
        assertEquals(emptyList(), listOf(1, 2) cross emptyList<String>())
    }

    @Test
    fun transpose_swapsRowsAndColumns() {
        assertEquals(
            listOf(listOf(1, 3, 5), listOf(2, 4, 6)),
            listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6)).transpose(),
        )
    }

    @Test
    fun transpose_usesShortestRow() {
        assertEquals(listOf(listOf(1, 3)), listOf(listOf(1, 2), listOf(3)).transpose())
    }

    @Test
    fun transpose_ofEmpty_isEmpty() {
        assertEquals(emptyList(), emptyList<List<Int>>().transpose())
    }
}
