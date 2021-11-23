package com.juul.krayon.selection

import com.juul.krayon.element.Element

public fun <E: Element, D1, D2> Selection<E, D1>.data(
    value: List<D2>,
): UpdateSelection<E, D2> = data { _, _ -> value }

public fun <E: Element, D1, D2> Selection<E, D1>.data(
    value: (index: Int, group: Group<E, D1>) -> List<D2>,
): UpdateSelection<E, D2> {
    val update = ArrayList<Group<E, D2>>(groups.size)
    val enter = ArrayList<Group<EnterElement, D2>>(groups.size)
    val exit = ArrayList<Group<E, D2>>(groups.size)

    for ((index, group) in groups.withIndex()) {
        val data = value(index, group)
        val (updateNodes, enterNodes, exitNodes) = bindIndex(group, data)
        update += Group(group.parent, updateNodes)
        enter += Group(group.parent, enterNodes)
        exit += Group(group.parent, exitNodes)

        // Associate enter elements with their next update element
        var updateIndex = 0
        for ((enterIndex, node) in enterNodes.withIndex()) {
            if (node != null) {
                if (enterIndex >= updateIndex) {
                    updateIndex = enterIndex + 1
                }
                node.next = updateNodes.subList(updateIndex, updateNodes.size)
                    .onEach { if (it == null) updateIndex += 1 }
                    .filterNotNull()
                    .firstOrNull()
            }
        }
    }

    return UpdateSelection(update, EnterSelection(enter), ExitSelection(exit))
}

private fun <E: Element, D1, D2> bindIndex(
    group: Group<E, D1>,
    data: List<D2>,
): Triple<List<E?>, List<EnterElement?>, List<E?>> {
    val update = ArrayList<E?>(data.size)
    val enter = ArrayList<EnterElement?>(data.size)
    val exit = ArrayList<E?>(maxOf(data.size, group.nodes.size))
    data.withIndex().forEach { (index, value) ->
        val node = group.nodes.getOrNull(index)
        if (node != null) {
            update.add(node.also { it.data = value })
            enter.add(null)
        } else {
            update.add(null)
            enter.add(
                EnterElement().also {
                    it.data = value
                    it.parent = group.parent
                }
            )
        }
        exit.add(null)
    }
    if (group.nodes.size > data.size) {
        group.nodes
            .subList(data.size, group.nodes.size)
            .forEach { node ->
                exit.add(node)
            }
    }
    return Triple(update, enter, exit)
}
