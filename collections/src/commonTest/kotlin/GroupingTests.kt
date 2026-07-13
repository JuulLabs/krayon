package com.juul.krayon.collections

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

class GroupingTests {

    private data class Row(val name: String, val group: String, val value: Int)

    private val rows = listOf(
        Row("a", "x", 1),
        Row("b", "x", 2),
        Row("c", "y", 3),
    )

    @Test
    fun group_bySingleKey() {
        assertEquals(
            mapOf("x" to listOf(rows[0], rows[1]), "y" to listOf(rows[2])),
            rows.group { it.group },
        )
    }

    @Test
    fun group_byTwoKeys_nestsMaps() {
        val grouped = rows.group({ it.group }, { it.name })
        assertEquals(listOf(rows[0]), grouped.getValue("x").getValue("a"))
        assertEquals(listOf(rows[2]), grouped.getValue("y").getValue("c"))
    }

    @Test
    fun groups_returnsListOfPairs() {
        assertEquals(
            listOf("x" to listOf(rows[0], rows[1]), "y" to listOf(rows[2])),
            rows.groups { it.group },
        )
    }

    @Test
    fun index_byUniqueKey() {
        assertEquals(mapOf("a" to rows[0], "b" to rows[1], "c" to rows[2]), rows.index { it.name })
    }

    @Test
    fun index_withDuplicateKey_throws() {
        assertFailsWith<IllegalArgumentException> { rows.index { it.group } }
    }

    @Test
    fun rollup_reducesGroups() {
        assertEquals(mapOf("x" to 3, "y" to 3), rows.rollup(key = { it.group }) { group -> group.sumOf { it.value } })
    }

    @Test
    fun rollups_returnsListOfPairs() {
        assertEquals(listOf("x" to 3, "y" to 3), rows.rollups(key = { it.group }) { group -> group.sumOf { it.value } })
    }

    @Test
    fun rollup_byTwoKeys() {
        val result = rows.rollup({ it.group }, { it.name }) { group -> group.sumOf { it.value } }
        assertEquals(1, result.getValue("x").getValue("a"))
        assertEquals(2, result.getValue("x").getValue("b"))
    }

    @Test
    fun flatGroup_byTwoKeys() {
        val result = rows.flatGroup({ it.group }, { it.name })
        assertEquals(3, result.size)
        assertEquals(Triple("x", "a", listOf(rows[0])), result[0])
    }

    @Test
    fun flatRollup_byTwoKeys() {
        val result = rows.flatRollup({ it.group }, { it.name }) { group -> group.sumOf { it.value } }
        assertEquals(Triple("x", "a", 1), result[0])
    }

    @Test
    fun count_countsValidNumbers() {
        assertEquals(2, listOf(1.0, Double.NaN, 3.0).count { it })
    }
}
