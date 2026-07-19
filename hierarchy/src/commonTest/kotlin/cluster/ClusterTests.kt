package com.juul.krayon.hierarchy.cluster

import com.juul.krayon.hierarchy.Point
import com.juul.krayon.hierarchy.fixtureA
import com.juul.krayon.hierarchy.fixtureB
import com.juul.krayon.hierarchy.layoutByName
import kotlin.test.Test
import kotlin.test.assertEquals

private const val TOLERANCE = 1e-4f

private fun assertPoint(expectedX: Float, expectedY: Float, actual: Point) {
    assertEquals(expectedX, actual.x, TOLERANCE)
    assertEquals(expectedY, actual.y, TOLERANCE)
}

class ClusterTests {

    @Test
    fun cluster_size_matchesD3() {
        val layout = fixtureA().layoutWith(Cluster(width = 1f, height = 1f)).layoutByName()
        assertPoint(0.55f, 0f, layout.getValue("root"))
        assertPoint(0.3f, 0.5f, layout.getValue("a"))
        assertPoint(0.8f, 0.5f, layout.getValue("b"))
        assertPoint(0.2f, 1f, layout.getValue("a1"))
        assertPoint(0.4f, 1f, layout.getValue("a2"))
        assertPoint(0.8f, 1f, layout.getValue("b1"))
    }

    @Test
    fun cluster_nodeSize_matchesD3() {
        val layout = fixtureA().layoutWith(Cluster(width = 2f, height = 3f, nodeSize = true)).layoutByName()
        assertPoint(0f, 0f, layout.getValue("root"))
        assertPoint(-2.5f, 3f, layout.getValue("a"))
        assertPoint(2.5f, 3f, layout.getValue("b"))
        assertPoint(-3.5f, 6f, layout.getValue("a1"))
        assertPoint(-1.5f, 6f, layout.getValue("a2"))
        assertPoint(2.5f, 6f, layout.getValue("b1"))
    }

    @Test
    fun cluster_unevenDepth_alignsLeavesToBottom() {
        val layout = fixtureB().layoutWith(Cluster(width = 1f, height = 1f)).layoutByName()
        assertPoint(0.5f, 0f, layout.getValue("root"))
        assertPoint(0.25f, 0.333333f, layout.getValue("a"))
        assertPoint(0.75f, 1f, layout.getValue("b"))
        assertPoint(0.25f, 0.666667f, layout.getValue("a1"))
        assertPoint(0.25f, 1f, layout.getValue("a1x"))
    }
}
