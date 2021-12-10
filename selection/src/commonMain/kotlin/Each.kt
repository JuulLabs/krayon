package com.juul.krayon.selection

import com.juul.krayon.element.Element

/**
 * See analogous [d3 function](https://github.com/d3/d3-selection#selection_each).
 *
 * Note that this serves triple-duty for [d3 attr](https://github.com/d3/d3-selection#selection_attr)
 * and [d3 style](https://github.com/d3/d3-selection#selection_style) due to strong typing and a lack
 * of CSS, respectively.
 */
public inline fun <E : Element, D, S : Selection<E, D>> S.each(
    crossinline action: E.(Arguments<D, E?>) -> Unit,
): S {
    val arguments = Arguments.Buffer<D, E?>()
    groups.forEach { group ->
        group.nodes.forEachIndexed { index, node ->
            node?.action(arguments(node.data as D, index, group.nodes))
        }
    }
    return this
}
