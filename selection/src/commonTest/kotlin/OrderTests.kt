package com.juul.krayon.selection

import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals
import kotlin.test.assertSame
import kotlin.test.assertTrue

class OrderTests {

    @Test
    fun order_returnsSameSelectionAsReceiver() {
        val root = RootElement()
        val one = TextElement().apply { text = "1" }.also { root.appendChild(it) }
        val two = TextElement().apply { text = "2" }.also { root.appendChild(it) }
        val selection = listOf(two, one).asSelection()
        assertSame(expected = selection, actual = selection.order())
    }

    @Test
    fun order_withSingleGroup_setsParentsChildrenInOrder() {
        val root = RootElement()
        val one = TextElement().apply { text = "1" }.also { root.appendChild(it) }
        val two = TextElement().apply { text = "2" }.also { root.appendChild(it) }
        assertContentEquals(expected = listOf(one, two), actual = root.children)
        listOf(two, one).asSelection().order()
        assertContentEquals(expected = listOf(two, one), actual = root.children)
    }

    @Test
    fun order_withNestedElementsAndMultipleGroups_setsEachParentsChildrenInOrder() {
        val root = RootElement()
        val odds = GroupElement().also { root.appendChild(it) }
        val evens = GroupElement().also { root.appendChild(it) }
        val one = TextElement().apply { text = "1" }.also { odds.appendChild(it) }
        val two = TextElement().apply { text = "2" }.also { evens.appendChild(it) }
        val three = TextElement().apply { text = "3" }.also { odds.appendChild(it) }
        val four = TextElement().apply { text = "4" }.also { evens.appendChild(it) }
        assertContentEquals(expected = listOf(one, three), actual = odds.children)
        assertContentEquals(expected = listOf(two, four), actual = evens.children)

        Selection<TextElement, Nothing?>(
            groups = listOf(
                Group(odds, listOf(three, one)),
                Group(evens, listOf(four, two)),
            ),
        ).order()

        assertContentEquals(expected = listOf(three, one), actual = odds.children)
        assertContentEquals(expected = listOf(four, two), actual = evens.children)
    }

    @Test
    fun order_withFlatElementsAndMultipleGroups_sortsWithinEachGroup() {
        val one = TextElement().apply { text = "1" }
        val two = TextElement().apply { text = "2" }
        val three = TextElement().apply { text = "3" }
        val four = TextElement().apply { text = "4" }
        val five = TextElement().apply { text = "5" }

        val elements = listOf(one, two, three, four, five)
        for (permutation in elements.permutations()) {
            val root = RootElement()

            permutation.forEach { element -> root.appendChild(element) }

            Selection<TextElement, Nothing?>(
                groups = listOf(
                    Group(root, listOf(three, one)),
                    Group(root, listOf(four, two)),
                ),
            ).order()

            // `order` doesn't guarantee an ordering of groups within the list, so just check within each group
            assertTrue { root.children.indexOf(three) < root.children.indexOf(one) }
            assertTrue { root.children.indexOf(four) < root.children.indexOf(two) }

            // Sanity check that we didn't lose anything
            assertEquals(setOf(one, two, three, four, five), root.children.toSet())
        }
    }

    @Test
    fun permutations_returnsAllPermutationsOfElements() {
        val elements = listOf(1, 2, 3)
        assertEquals(
            expected = setOf(listOf(1, 2, 3), listOf(1, 3, 2), listOf(2, 1, 3), listOf(2, 3, 1), listOf(3, 1, 2), listOf(3, 2, 1)),
            actual = elements.permutations().toSet(),
        )
    }
}

/** Test utility which returns all possible sort orders for the input list. */
private fun <T> List<T>.permutations(): Sequence<List<T>> {
    if (size <= 1) return sequenceOf(this)

    return sequence {
        val input = this@permutations
        input.forEachIndexed { index, item ->
            val remaining = ArrayList(input).apply { removeAt(index) }
            for (permutation in remaining.permutations()) {
                yield(permutation + item)
            }
        }
    }
}
