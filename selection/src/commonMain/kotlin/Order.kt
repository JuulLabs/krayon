package com.juul.krayon.selection

import com.juul.krayon.element.Element

public fun <E: Element, D> Selection<E, D>.order(): Selection<E, D> {
    // TODO: This could be optimized to not move stuff around if things are already in place.
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
