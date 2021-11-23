package com.juul.krayon.selection

import com.juul.krayon.element.Element

public inline fun <T> Selection<T>.select(
    crossinline select: Element.(datum: T, index: Int, group: Group<T>) -> Element?,
): Selection<T> = Selection(
    groups.map { group ->
        Group(
            group.parent,
            group.nodes
                .withIndex()
                .map { (index, node) ->
                    node?.select(node.data as T, index, group)
                        ?.also { it.data = node.data }
                }
        )
    }
)
