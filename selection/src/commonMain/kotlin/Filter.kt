package com.juul.krayon.selection

import com.juul.krayon.element.Element

public inline fun <E : Element, D> Selection<E, D>.filter(
    crossinline filter: E.(Arguments<D, E?>) -> Boolean,
): Selection<E, D> {
    val arguments = Arguments.Buffer<D, E?>()
    return Selection(
        groups.map { group ->
            Group(
                group.parent,
                group.nodes.mapIndexedNotNull { index, node ->
                    node?.takeIf {
                        @Suppress("UNCHECKED_CAST")
                        node.filter(arguments(node.data as D, index, group.nodes))
                    }
                },
            )
        },
    )
}
