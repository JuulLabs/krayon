package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.ElementSelector

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_select). */
public fun <E1 : Element, E2 : Element, D> Selection<E1, D>.select(
    selector: ElementSelector<E2>,
): Selection<E2, D> = select { query(selector) }

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_select). */
public inline fun <E1 : Element, E2 : Element, D> Selection<E1, D>.select(
    crossinline select: E1.(Arguments<D, E1?>) -> E2?,
): Selection<E2, D> {
    val arguments = Arguments.Buffer<D, E1?>()
    return Selection(
        groups.map { group ->
            Group(
                group.parent,
                group.nodes.mapIndexed { index, node ->
                    node?.select(arguments(node.data as D, index, group.nodes))
                        ?.also { it.data = node.data }
                }
            )
        }
    )
}
