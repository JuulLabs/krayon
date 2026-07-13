package com.juul.krayon.collections

import kotlin.random.Random
import kotlin.test.Test
import kotlin.test.assertEquals

class TransformationsTests {

    @Test
    fun cross_producesCartesianProduct() {
        assertEquals(
            listOf(1 to "a", 1 to "b", 2 to "a", 2 to "b"),
            cross(listOf(1, 2), listOf("a", "b")),
        )
    }

    @Test
    fun cross_withReducer_appliesReduce() {
        assertEquals(listOf("1a", "1b", "2a", "2b"), cross(listOf(1, 2), listOf("a", "b")) { a, b -> "$a$b" })
    }

    @Test
    fun merge_flattensIterables() {
        assertEquals(listOf(1, 2, 3, 4), merge(listOf(listOf(1), listOf(2, 3), listOf(4))))
    }

    @Test
    fun pairs_returnsAdjacentPairs() {
        assertEquals(listOf(1 to 2, 2 to 3, 3 to 4), listOf(1, 2, 3, 4).pairs())
    }

    @Test
    fun pairs_withTransform() {
        assertEquals(listOf(1, 1, 1), listOf(1, 2, 3, 4).pairs { a, b -> b - a })
    }

    @Test
    fun pairs_ofSingleElement_isEmpty() {
        assertEquals(emptyList(), listOf(1).pairs())
    }

    @Test
    fun permute_reordersByIndices() {
        assertEquals(listOf("c", "a", "b"), permute(listOf("a", "b", "c"), listOf(2, 0, 1)))
    }

    @Test
    fun permute_ofMapByKeys() {
        assertEquals(listOf(2, 1), permute(mapOf("a" to 1, "b" to 2), listOf("b", "a")))
    }

    @Test
    fun transpose_swapsRowsAndColumns() {
        assertEquals(listOf(listOf(1, 3, 5), listOf(2, 4, 6)), transpose(listOf(listOf(1, 2), listOf(3, 4), listOf(5, 6))))
    }

    @Test
    fun transpose_usesShortestRow() {
        assertEquals(listOf(listOf(1, 3)), transpose(listOf(listOf(1, 2), listOf(3))))
    }

    @Test
    fun zip_transposesArguments() {
        assertEquals(listOf(listOf(1, 3, 5), listOf(2, 4, 6)), zip(listOf(1, 2), listOf(3, 4), listOf(5, 6)))
    }

    @Test
    fun shuffle_isPermutationOfInput() {
        val original = (0 until 100).toList()
        val list = original.toMutableList()
        shuffle(list, random = Random(42))
        assertEquals(original.toSet(), list.toSet())
        assertEquals(original.size, list.size)
    }

    @Test
    fun shuffler_shufflesSubrangeOnly() {
        val list = mutableListOf(0, 1, 2, 3, 4, 5)
        shuffler(Random(1)).shuffle(list, from = 1, to = 4)
        assertEquals(0, list[0])
        assertEquals(4, list[4])
        assertEquals(5, list[5])
        assertEquals(setOf(1, 2, 3), setOf(list[1], list[2], list[3]))
    }
}
