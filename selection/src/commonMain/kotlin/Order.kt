package com.juul.krayon.selection

import com.juul.krayon.element.Element

public fun <E : Element, D> Selection<E, D>.order(): Selection<E, D> {
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
