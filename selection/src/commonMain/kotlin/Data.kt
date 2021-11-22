package com.juul.krayon.selection

import com.juul.krayon.element.Element

public fun <P, C> Selection<P>.data(
    value: List<C>,
): UpdateSelection<C> = data { _, _ -> value }

public fun <P, C> Selection<P>.data(
    value: (index: Int, group: Group<P>) -> List<C>,
): UpdateSelection<C> {
    val update = ArrayList<Group<C>>(groups.size)
    val enter = ArrayList<Group<C>>(groups.size)
    val exit = ArrayList<Group<C>>(groups.size)

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

private fun <P, C> bindIndex(
    group: Group<P>,
    data: List<C>,
): Triple<List<Element?>, List<EnterElement?>, List<Element?>> {
    val update = ArrayList<Element?>(data.size)
    val enter = ArrayList<EnterElement?>(data.size)
    val exit = ArrayList<Element?>(maxOf(data.size, group.nodes.size))
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
