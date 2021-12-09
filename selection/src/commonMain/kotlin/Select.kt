package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.TypeSelector
import com.juul.krayon.element.descendants

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_select). */
public fun <E1 : Element, E2 : Element, D> Selection<E1, D>.select(
    selector: TypeSelector<E2>,
): Selection<E2, D> = select { descendants.mapNotNull { selector.trySelect(it) }.firstOrNull() }

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_select). */
public inline fun <E1 : Element, E2 : Element, D> Selection<E1, D>.select(
    crossinline select: E1.(Arguments<D, E1?>) -> E2?,
): Selection<E2, D> = Selection(
    groups.map { group ->
        Group(
            group.parent,
            group.nodes
                .withIndex()
                .map { (index, node) ->
                    node?.select(Arguments(node.data as D, index, group.nodes))
                        ?.also { it.data = node.data }
                }
        )
    }
)
