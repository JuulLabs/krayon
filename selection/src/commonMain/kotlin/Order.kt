package com.juul.krayon.selection

import com.juul.krayon.element.Element

/** See analogous [d3 function](https://d3js.org/d3-selection/modifying#selection_order). */
public fun <E : Element, D, S : Selection<E, D>> S.order(): S {
    groups.asSequence()
        .filter { it.parent != null }
        .groupingBy { it.parent!! }
        .aggregate { _, accumulator: MutableSet<E>?, group, _ ->
            (accumulator ?: LinkedHashSet()).apply {
                group.nodes.forEach { element -> if (element != null) add(element) }
            }
        }.forEach { (parent, elements) ->
            // TODO: Instead of this cheat, add a @KrayonInternal annotation and expose backing field
            val children = parent.children as MutableList<Element>
            children.retainAll { it !in elements }
            children.addAll(elements)
        }
    return this
}
