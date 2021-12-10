package com.juul.krayon.selection

import com.juul.krayon.element.Element

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_data). */
public fun <E : Element, D1, D2> Selection<E, D1>.data(
    value: List<D2>,
): UpdateSelection<E, D2> = data { _, _ -> value }

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_data). */
public fun <E : Element, D1, D2> Selection<E, D1>.data(
    value: (index: Int, group: Group<E, D1>) -> List<D2>,
): UpdateSelection<E, D2> = dataImpl(value, null)

/**
 * See analogous [d3 function](https://github.com/d3/d3-selection#selection_data).
 *
 * TODO: Figure out a way to strongly type [key], even though it's called with
 *       two different sets of argument types.
 */
public fun <E : Element, D1, D2> Selection<E, D1>.keyedData(
    value: List<D2>,
    key: Element?.(Arguments<Any?, Any?>) -> Any?,
): UpdateSelection<E, D2> =
    keyedData(value = { _, _ -> value }, key)

/**
 * See analogous [d3 function](https://github.com/d3/d3-selection#selection_data).
 *
 * TODO: Figure out a way to strongly type [key], even though it's called with
 *       two different sets of argument types.
 */
public fun <E : Element, D1, D2> Selection<E, D1>.keyedData(
    value: (index: Int, group: Group<E, D1>) -> List<D2>,
    key: Element?.(Arguments<Any?, Any?>) -> Any?,
): UpdateSelection<E, D2> = dataImpl(value, key)

private fun <E : Element, D1, D2> Selection<E, D1>.dataImpl(
    value: (index: Int, group: Group<E, D1>) -> List<D2>,
    key: (Element?.(Arguments<Any?, Any?>) -> Any?)?,
): UpdateSelection<E, D2> {
    val update = ArrayList<Group<E, D2>>(groups.size)
    val enter = ArrayList<Group<EnterElement, D2>>(groups.size)
    val exit = ArrayList<Group<E, D2>>(groups.size)

    for ((index, group) in groups.withIndex()) {
        val data = value(index, group)
        val (updateNodes, enterNodes, exitNodes) = when (key) {
            null -> bindIndex(group, data)
            else -> bindKey(group, data, key)
        }
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

private fun <E : Element, D1, D2> bindIndex(
    group: Group<E, D1>,
    data: List<D2>,
): Triple<List<E?>, List<EnterElement?>, List<E?>> {
    val update = ArrayList<E?>(data.size)
    val enter = ArrayList<EnterElement?>(data.size)
    val exit = ArrayList<E?>(maxOf(data.size, group.nodes.size))
    data.forEachIndexed { index, value ->
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

private fun <E : Element, D1, D2> bindKey(
    group: Group<E, D1>,
    data: List<D2>,
    key: Element?.(Arguments<Any?, Any?>) -> Any?,
): Triple<List<E?>, List<EnterElement?>, List<E?>> {
    val update = ArrayList<E?>(data.size)
    val enter = ArrayList<EnterElement?>(data.size)
    val exit = ArrayList<E?>(group.nodes.size)

    val nodeByKeyValue = HashMap<Any?, E>()
    val keys = ArrayList<Any?>(group.nodes.size)

    // Compute keys for existing nodes.
    // If multiple nodes share a key, nodes after the first are added to exit.
    group.nodes.forEachIndexed { index, node ->
        if (node == null) {
            keys.add(null)
            exit.add(null)
        } else {
            val keyValue = node.key(Arguments(node.data, index, group.nodes))
            keys.add(keyValue)
            if (keyValue in nodeByKeyValue) {
                exit.add(node)
            } else {
                exit.add(null)
                nodeByKeyValue[keyValue] = node
            }
        }
    }

    // Compute keys for data.
    // Matching nodes are added to update and removed from the key map.
    // If no node matches, add it to enter.
    // If multiple datums share a key, datums after the first are added to enter.
    data.forEachIndexed { index, value ->
        val keyValue = group.parent.key(Arguments(value, index, data))
        val node = nodeByKeyValue[keyValue]
        if (node == null) {
            enter.add(
                EnterElement().also {
                    it.parent = group.parent
                    it.data = value
                }
            )
            update.add(null)
            nodeByKeyValue.remove(keyValue)
        } else {
            enter.add(null)
            update.add(
                node.also {
                    it.data = value
                }
            )
        }
    }

    // Nodes that still exist in the key map are inserted into exit.
    group.nodes.forEachIndexed { index, node ->
        if (node != null && nodeByKeyValue[keys[index]] === node) {
            exit[index] = node
        }
    }

    return Triple(update, enter, exit)
}
