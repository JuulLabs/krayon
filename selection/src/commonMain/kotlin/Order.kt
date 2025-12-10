package com.juul.krayon.selection

import com.juul.krayon.element.Element

/** See analogous [d3 function](https://d3js.org/d3-selection/modifying#selection_order). */
public fun <E : Element, D, S : Selection<E, D>> S.order(): S {
    for (group in groups) {
        val parent = group.parent ?: continue
        val nodes = group.nodes

        var next: E? = null
        for (index in nodes.lastIndex downTo 0) {
            val node = nodes[index] ?: continue

            if (next != null) {
                val children = parent.children
                val nodeIndex = children.indexOf(node)
                val nextIndex = children.indexOf(next)

                if (nodeIndex > nextIndex) {
                    parent.insertBefore(node, next)
                }
            }

            next = node
        }
    }

    return this
}
