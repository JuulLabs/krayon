package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.descendents

public inline fun <T> Selection<T>.selectAll(
    crossinline select: Element.(datum: T, index: Int, group: Group<T>) -> List<Element>,
): Selection<T> = Selection(
    groups.flatMap { group ->
        group.nodes.asSequence()
            .withIndex()
            .filter { (_, node) -> node != null }
            .map { (index, node) ->
                node as Element
                Group(
                    node,
                    node.select(node.data as T, index, group)
                        .onEach { it.data = node.data }
                )
            }
    }
)

public inline fun <T> Selection<T>.selectAllDescendents(
    crossinline select: Element.() -> Boolean,
): Selection<T> = selectAll { _, _, _ ->
    descendents.filter { it.select() }.toList()
}
