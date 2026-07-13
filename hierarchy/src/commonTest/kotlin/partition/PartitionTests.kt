package com.juul.krayon.hierarchy.partition

import com.juul.krayon.hierarchy.fixtureNested
import com.juul.krayon.hierarchy.layoutByName
import com.juul.krayon.hierarchy.sum
import kotlin.test.Test
import kotlin.test.assertEquals

private const val TOLERANCE = 1e-4f

private fun assertCell(x0: Float, y0: Float, x1: Float, y1: Float, actual: Cell) {
    assertEquals(x0, actual.x0, TOLERANCE)
    assertEquals(y0, actual.y0, TOLERANCE)
    assertEquals(x1, actual.x1, TOLERANCE)
    assertEquals(y1, actual.y1, TOLERANCE)
}

class PartitionTests {

    @Test
    fun partition_size_matchesD3() {
        val layout = fixtureNested().sum { it.value }
            .layoutWith(Partition(width = 6f, height = 4f))
            .layoutByName()
        assertCell(0f, 0f, 6f, 1.333333f, layout.getValue("root"))
        assertCell(0f, 1.333333f, 3f, 2.666667f, layout.getValue("a"))
        assertCell(3f, 1.333333f, 6f, 2.666667f, layout.getValue("b"))
        assertCell(0f, 2.666667f, 1f, 4f, layout.getValue("a1"))
        assertCell(1f, 2.666667f, 3f, 4f, layout.getValue("a2"))
        assertCell(3f, 2.666667f, 6f, 4f, layout.getValue("b1"))
    }

    @Test
    fun partition_padding_matchesD3() {
        val layout = fixtureNested().sum { it.value }
            .layoutWith(Partition(width = 6f, height = 4f, padding = 0.5f))
            .layoutByName()
        assertCell(0.5f, 0.5f, 5.5f, 0.833333f, layout.getValue("root"))
        assertCell(0.5f, 1.333333f, 2.75f, 2.166667f, layout.getValue("a"))
        assertCell(3.25f, 1.333333f, 5.5f, 2.166667f, layout.getValue("b"))
        assertCell(0.5f, 2.666667f, 0.916667f, 3.5f, layout.getValue("a1"))
        assertCell(1.416667f, 2.666667f, 2.75f, 3.5f, layout.getValue("a2"))
        assertCell(3.25f, 2.666667f, 5.5f, 3.5f, layout.getValue("b1"))
    }

    @Test
    fun partition_round_snapsToIntegers() {
        val layout = fixtureNested().sum { it.value }
            .layoutWith(Partition(width = 6f, height = 4f, round = true))
            .layoutByName()
        assertCell(0f, 0f, 6f, 1f, layout.getValue("root"))
        assertCell(0f, 1f, 3f, 3f, layout.getValue("a"))
        assertCell(3f, 1f, 6f, 3f, layout.getValue("b"))
        assertCell(0f, 3f, 1f, 4f, layout.getValue("a1"))
        assertCell(1f, 3f, 3f, 4f, layout.getValue("a2"))
        assertCell(3f, 3f, 6f, 4f, layout.getValue("b1"))
    }
}
