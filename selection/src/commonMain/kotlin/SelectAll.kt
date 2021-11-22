package com.juul.krayon.selection

import com.juul.krayon.element.Element

public inline fun <T> Selection<T>.selectAll(
    crossinline select: Element.(datum: T, index: Int, nodes: List<Element?>) -> List<Element>,
): Selection<T> = Selection(
    groups.flatMap { group ->
        group.nodes.asSequence()
            .withIndex()
            .filter { (_, node) -> node != null }
            .map { (index, node) ->
                node as Element
                Group(
                    node,
                    node.select(node.data as T, index, group.nodes)
                        .onEach { it.data = node.data }
                )
            }
    }
)
