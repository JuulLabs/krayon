package com.juul.krayon.hierarchy

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import kotlin.test.assertSame

class NodeHelpersTests {

    private fun Node<Datum, Nothing?>.byName() = traverseBreadthFirst().associateBy { it.data.name }

    @Test
    fun descendants_areBreadthFirst() {
        val names = fixtureA().descendants().map { it.data.name }
        assertEquals(listOf("root", "a", "b", "a1", "a2", "b1"), names)
    }

    @Test
    fun leaves_arePreOrderLeaves() {
        val names = fixtureA().leaves().map { it.data.name }
        assertEquals(listOf("a1", "a2", "b1"), names)
    }

    @Test
    fun links_connectParentToChildBreadthFirst() {
        val edges = fixtureA().links().map { it.source.data.name to it.target.data.name }
        assertEquals(
            listOf("root" to "a", "root" to "b", "a" to "a1", "a" to "a2", "b" to "b1"),
            edges,
        )
    }

    @Test
    fun path_passesThroughLeastCommonAncestor() {
        val root = fixtureA()
        val byName = root.byName()
        val names = byName.getValue("a1").path(byName.getValue("b1")).map { it.data.name }
        assertEquals(listOf("a1", "a", "root", "b", "b1"), names)
    }

    @Test
    fun path_toSelfIsSingleton() {
        val root = fixtureA()
        val a1 = root.byName().getValue("a1")
        assertEquals(listOf("a1"), a1.path(a1).map { it.data.name })
    }

    @Test
    fun copy_deepCopiesStructureSharingData() {
        val root = fixtureA().count()
        val copy = root.copy()
        assertNotSame(root, copy)
        assertNull(copy.parent)
        assertEquals(6f, copy.weight)
        assertEquals(
            root.descendants().map { it.data.name },
            copy.descendants().map { it.data.name },
        )
        assertSame(root.data, copy.data)
        assertSame(root.children.first().data, copy.children.first().data)
        assertSame(copy, copy.children.first().parent)
    }
}
