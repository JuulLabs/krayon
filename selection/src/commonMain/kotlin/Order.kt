package com.juul.krayon.selection

import com.juul.krayon.element.Element

/** See analogous [d3 function](https://d3js.org/d3-selection/modifying#selection_order). */
public fun <E : Element, D, S : Selection<E, D>> S.order(): S {
    for (group in groups) {
        val parent = group.parent ?: continue
        val nodes = group.nodes

        lateinit var next: E
        var nextIndexInParent = Int.MAX_VALUE
        for (indexInGroup in nodes.lastIndex downTo 0) {
            val node = nodes[indexInGroup] ?: continue
            val nodeIndexInParent = parent.children.indexOf(node)

            if (nodeIndexInParent > nextIndexInParent) {
                parent.insertBefore(node, next)
                nextIndexInParent -= 1
            } else {
                nextIndexInParent = nodeIndexInParent
            }

            next = node
        }
    }

    return this
}
