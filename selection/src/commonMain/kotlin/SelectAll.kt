package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.TypeSelector
import com.juul.krayon.element.descendents

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_selectAll). */
public fun <E1 : Element, E2 : Element, D> Selection<E1, D>.selectAll(
    selector: TypeSelector<E2>,
): Selection<E2, D> = selectAll { descendents.mapNotNull { selector.trySelect(it) } }

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_selectAll). */
public inline fun <E1 : Element, E2 : Element, D> Selection<E1, D>.selectAll(
    crossinline select: E1.(Arguments<E1, D>) -> Sequence<E2>,
): Selection<E2, D> = Selection(
    groups.flatMap { group ->
        group.nodes.asSequence()
            .withIndex()
            .filter { (_, node) -> node != null }
            .map { (index, node) ->
                node as Element
                Group(
                    node,
                    node.select(Arguments(node.data as D, index, group.nodes))
                        .onEach { it.data = node.data }
                        .toList()
                )
            }
    }
)
