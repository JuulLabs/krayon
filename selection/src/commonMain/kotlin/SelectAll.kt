package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.TypeSelector
import com.juul.krayon.element.descendents

public fun <E1 : Element, E2 : Element, D> Selection<E1, D>.selectAll(
    selector: TypeSelector<E2>,
): Selection<E2, D> = selectAll { _, _, _ -> descendents.mapNotNull { selector.trySelect(it) } }

public inline fun <E1 : Element, E2 : Element, D> Selection<E1, D>.selectAll(
    crossinline select: Element.(datum: D, index: Int, group: List<E1?>) -> Sequence<E2>,
): Selection<E2, D> = Selection(
    groups.flatMap { group ->
        group.nodes.asSequence()
            .withIndex()
            .filter { (_, node) -> node != null }
            .map { (index, node) ->
                node as Element
                Group(
                    node,
                    node.select(node.data as D, index, group.nodes)
                        .onEach { it.data = node.data }
                        .toList()
                )
            }
    }
)
