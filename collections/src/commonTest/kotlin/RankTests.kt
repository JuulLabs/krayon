package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals

class RankTests {

    @Test
    fun rank_withTies_sharesSmallestRank() {
        assertEquals(listOf(3, 0, 2, 0, 4), listOf(3, 1, 2, 1, 5).rank { it }.toList())
    }

    @Test
    fun rank_ofStrings_matchesD3() {
        assertEquals(listOf(1, 0, 2), listOf("b", "a", "c").rank { it }.toList())
    }

    @Test
    fun rank_withComparator_matchesSelector() {
        assertEquals(listOf(3, 0, 2, 0, 4), listOf(3, 1, 2, 1, 5).rank(naturalOrder()).toList())
    }

    @Test
    fun rank_ofEmpty_isEmpty() {
        assertEquals(emptyList(), emptyList<Int>().rank { it }.toList())
    }
}
