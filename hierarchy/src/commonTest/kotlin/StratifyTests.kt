package com.juul.krayon.hierarchy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNull

private data class Row(val id: String, val parentId: String? = null)

class StratifyTests {

    private val table = listOf(
        Row("root"),
        Row("a", "root"),
        Row("b", "root"),
        Row("a1", "a"),
    )

    private fun stratifyTable(rows: List<Row>) =
        stratify(rows, id = { it.id }, parentId = { it.parentId })

    @Test
    fun stratify_buildsExpectedStructure() {
        val root = stratifyTable(table)
        assertEquals("root", root.data.id)
        assertNull(root.parent)
        assertEquals(0, root.depth)
        assertEquals(2, root.height)

        val byName = root.traverseBreadthFirst().associateBy { it.data.id }
        assertEquals("root", byName.getValue("a").parent?.data?.id)
        assertEquals("a", byName.getValue("a1").parent?.data?.id)
        assertEquals(1, byName.getValue("a").height)
        assertEquals(0, byName.getValue("b").height)
        assertEquals(2, byName.getValue("a1").depth)
        assertEquals(listOf("a", "b"), root.children.map { it.data.id })
    }

    @Test
    fun stratify_missingParent_fails() {
        assertFailsWith<IllegalArgumentException> {
            stratifyTable(listOf(Row("root"), Row("a", "missing")))
        }
    }

    @Test
    fun stratify_multipleRoots_fails() {
        assertFailsWith<IllegalArgumentException> {
            stratifyTable(listOf(Row("root"), Row("other")))
        }
    }

    @Test
    fun stratify_ambiguousId_fails() {
        assertFailsWith<IllegalArgumentException> {
            stratifyTable(listOf(Row("root"), Row("a", "root"), Row("a", "root")))
        }
    }

    @Test
    fun stratify_noRoot_fails() {
        assertFailsWith<IllegalArgumentException> {
            stratifyTable(listOf(Row("a", "b"), Row("b", "a")))
        }
    }
}
