package com.juul.krayon.selection

import com.juul.krayon.element.Element

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_merge). */
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
