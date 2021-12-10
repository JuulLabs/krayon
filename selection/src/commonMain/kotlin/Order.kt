package com.juul.krayon.selection

import com.juul.krayon.element.Element

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_order). */
public fun <E : Element, D, S: Selection<E, D>> S.order(): S {
    for (group in groups) {
        val parent = group.parent ?: continue
        for (node in group.nodes) {
            if (node != null) {
                parent.appendChild(parent.removeChild(node))
            }
        }
    }
    return this
}
