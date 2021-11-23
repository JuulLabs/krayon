package com.juul.krayon.selection

import com.juul.krayon.element.Element

public fun <E : Element, D> Selection<E, D>.merge(
    other: Selection<E, D>,
): Selection<E, D> = Selection(
    groups.mapIndexed { groupIndex, group ->
        Group(
            group.parent,
            group.nodes.mapIndexed { nodeIndex, node ->
                node ?: other.groups[groupIndex].nodes[nodeIndex]
            }
        )
    }
)
