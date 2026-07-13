package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class OrderingTests {

    private data class Item(val key: Int)

    @Test
    fun ascending_comparesNaturally() {
        assertTrue(ascending(1, 2) < 0)
        assertTrue(ascending(2, 1) > 0)
        assertEquals(0, ascending(1, 1))
    }

    @Test
    fun descending_reversesOrder() {
        assertTrue(descending(1, 2) > 0)
        assertTrue(descending(2, 1) < 0)
    }

    @Test
    fun ascendingKey_sortsBySelector() {
        val items = listOf(Item(3), Item(1), Item(2))
        assertEquals(listOf(Item(1), Item(2), Item(3)), items.sortedWith(ascendingKey { it.key }))
    }

    @Test
    fun descendingKey_sortsBySelector() {
        val items = listOf(Item(1), Item(3), Item(2))
        assertEquals(listOf(Item(3), Item(2), Item(1)), items.sortedWith(descendingKey { it.key }))
    }

    @Test
    fun greatest_withSelector() {
        assertEquals(Item(3), listOf(Item(3), Item(1), Item(2)).greatest { it.key })
    }

    @Test
    fun greatest_withComparator() {
        assertEquals(Item(3), listOf(Item(3), Item(1), Item(2)).greatest(ascendingKey { it.key }))
    }

    @Test
    fun least_withSelector() {
        assertEquals(Item(1), listOf(Item(3), Item(1), Item(2)).least { it.key })
    }

    @Test
    fun greatest_ofEmpty_isNull() {
        assertNull(emptyList<Item>().greatest { it.key })
    }

    @Test
    fun greatestIndex_matchesD3() {
        assertEquals(4, listOf(3, 1, 4, 1, 5).greatestIndex { it })
    }

    @Test
    fun leastIndex_matchesD3() {
        assertEquals(1, listOf(3, 1, 4, 1, 5).leastIndex { it })
    }

    @Test
    fun greatestIndex_withComparator() {
        assertEquals(4, listOf(3, 1, 4, 1, 5).greatestIndex(naturalOrder()))
    }

    @Test
    fun leastIndex_ofEmpty_isNegativeOne() {
        assertEquals(-1, emptyList<Int>().leastIndex { it })
    }
}
