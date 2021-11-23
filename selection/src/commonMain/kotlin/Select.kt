package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.TypeSelector
import com.juul.krayon.element.descendents

public fun <E1 : Element, E2 : Element, D> Selection<E1, D>.select(
    selector: TypeSelector<E2>,
): Selection<E2, D> = select { _, _, _ -> descendents.mapNotNull { selector.trySelect(it) }.firstOrNull() }

public inline fun <E1: Element, E2: Element, D> Selection<E1, D>.select(
    crossinline select: E1.(datum: D, index: Int, group: List<E1?>) -> E2?,
): Selection<E2, D> = Selection(
    groups.map { group ->
        Group(
            group.parent,
            group.nodes
                .withIndex()
                .map { (index, node) ->
                    node?.select(node.data as D, index, group.nodes)
                        ?.also { it.data = node.data }
                }
        )
    }
)
