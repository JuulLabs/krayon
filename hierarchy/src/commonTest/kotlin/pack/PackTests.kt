package com.juul.krayon.hierarchy.pack

import com.juul.krayon.hierarchy.Node
import com.juul.krayon.hierarchy.fixtureWeighted
import com.juul.krayon.hierarchy.layoutByName
import com.juul.krayon.hierarchy.sum
import com.juul.krayon.hierarchy.traversePreOrder
import kotlin.math.sqrt
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

private const val TOLERANCE = 1e-3f

private fun assertCircle(x: Float, y: Float, radius: Float, actual: Circle) {
    assertEquals(x, actual.x, TOLERANCE)
    assertEquals(y, actual.y, TOLERANCE)
    assertEquals(radius, actual.radius, TOLERANCE)
}

class PackTests {

    @Test
    fun pack_size_matchesD3() {
        val layout = fixtureWeighted().sum { it.value }
            .layoutWith(Pack(width = 100f, height = 100f))
            .layoutByName()
        assertCircle(50f, 50f, 50f, layout.getValue("root"))
        assertCircle(25.835198f, 49.372877f, 8.859644f, layout.getValue("a"))
        assertCircle(47.224271f, 49.372877f, 12.529429f, layout.getValue("b"))
        assertCircle(32.061995f, 72.763237f, 15.345354f, layout.getValue("c"))
        assertCircle(31.654693f, 23.438862f, 17.719288f, layout.getValue("d"))
        assertCircle(67.156709f, 74.840233f, 19.810767f, layout.getValue("e"))
    }

    @Test
    fun pack_padding_matchesD3() {
        val layout = fixtureWeighted().sum { it.value }
            .layoutWith(Pack(width = 100f, height = 100f, padding = { 3f }))
            .layoutByName()
        assertCircle(50f, 50f, 50f, layout.getValue("root"))
        assertCircle(25.635218f, 49.497577f, 7.910929f, layout.getValue("a"))
        assertCircle(47.412643f, 49.497577f, 11.187744f, layout.getValue("b"))
        assertCircle(32.622252f, 72.762863f, 13.702132f, layout.getValue("c"))
        assertCircle(32.3033f, 23.941636f, 15.821859f, layout.getValue("d"))
        assertCircle(66.647509f, 74.513431f, 17.689376f, layout.getValue("e"))
    }

    @Test
    fun pack_childrenAreContainedWithinRoot() {
        val root: Node<*, Circle> = fixtureWeighted().sum { it.value }
            .layoutWith(Pack(width = 100f, height = 100f))
        val enclosing = root.layout
        root.traversePreOrder().forEach { node ->
            val circle = node.layout
            val distance = sqrt(
                (circle.x - enclosing.x) * (circle.x - enclosing.x) +
                    (circle.y - enclosing.y) * (circle.y - enclosing.y),
            )
            assertTrue(distance + circle.radius <= enclosing.radius + 1e-2f, "node ${'$'}circle escapes enclosing circle")
        }
    }

    @Test
    fun pack_leafRadiiScaleWithSqrtOfWeight() {
        val layout = fixtureWeighted().sum { it.value }
            .layoutWith(Pack(width = 100f, height = 100f))
            .layoutByName()
        val ratio = layout.getValue("e").radius / layout.getValue("a").radius
        assertEquals(sqrt(5f) / sqrt(1f), ratio, 1e-2f)
    }
}
