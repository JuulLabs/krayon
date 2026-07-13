package com.juul.krayon.hierarchy

/**
 * Builds a hierarchy from a flat [data] collection, linking each element to its parent by matching the id returned by
 * [id] with the parent id returned by [parentId]. The element whose [parentId] is `null` or empty becomes the root.
 *
 * Throws [IllegalArgumentException] if there is no single root, if a referenced parent id is missing or ambiguous, or
 * if the links form a cycle.
 */
public fun <T> stratify(
    data: Iterable<T>,
    id: (T) -> String?,
    parentId: (T) -> String?,
): Node<T, Nothing?> {
    val elements = data.toList()

    val byId = HashMap<String, T>()
    for (element in elements) {
        val key = id(element)?.takeIf { it.isNotEmpty() } ?: continue
        require(key !in byId) { "ambiguous: $key" }
        byId[key] = element
    }

    val childrenByParent = HashMap<String, MutableList<T>>()
    var root: T? = null
    var rootCount = 0
    for (element in elements) {
        val parent = parentId(element)?.takeIf { it.isNotEmpty() }
        if (parent != null) {
            require(parent in byId) { "missing: $parent" }
            childrenByParent.getOrPut(parent) { mutableListOf() }.add(element)
        } else {
            root = element
            rootCount += 1
        }
    }
    require(rootCount != 0) { "no root" }
    require(rootCount == 1) { "multiple roots" }

    fun build(element: T, parent: Node<T, Nothing?>?): Node<T, Nothing?> {
        val node = Node(element, null, parent)
        val key = id(element)?.takeIf { it.isNotEmpty() }
        val children = key?.let { childrenByParent[it] }.orEmpty()
        node.children = children.map { build(it, node) }
        return node
    }

    val rootNode = build(checkNotNull(root), null)
    require(rootNode.traversePreOrder().count() == elements.size) { "cycle" }
    return rootNode
}
