package com.juul.krayon.selection

import com.juul.krayon.element.Element

public inline fun <E : Element, D, S : Selection<E, D>> S.each(
    crossinline action: E.(Arguments<E, D>) -> Unit,
): S {
    groups.forEach { group ->
        group.nodes.forEachIndexed { index, node ->
            node?.action(Arguments(node.data as D, index, group.nodes))
        }
    }
    return this
}
