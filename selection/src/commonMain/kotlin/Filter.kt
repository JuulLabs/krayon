package com.juul.krayon.selection

import com.juul.krayon.element.Element

public fun <E : Element, D> Selection<E, D>.filter(
    filter: E.(Arguments<D, E?>) -> Boolean,
): Selection<E, D> = Selection(
    groups.map { group ->
        Group(
            group.parent,
            group.nodes.mapIndexedNotNull { index, node ->
                node?.takeIf { node.filter(Arguments(node.data as D, index, group.nodes)) }
            }
        )
    }
)
