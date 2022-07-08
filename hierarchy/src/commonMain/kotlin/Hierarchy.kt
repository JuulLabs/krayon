package com.juul.krayon.hierarchy

public fun <T> hierarchy(
    root: T,
    getChildren: (T) -> List<T>,
): Node<T, Nothing?> {
    fun addChildren(parentNode: Node<T, Nothing?>) {
        val children = getChildren(parentNode.data)
        parentNode.children = children.map { child ->
            Node(child, null, parentNode).also(::addChildren)
        }
    }
    return Node(root, null).also(::addChildren)
}

public fun <T> flatHierarchy(
    values: Iterable<T>,
): Node<T?, Nothing?> = Node(null as T?, null).also { root ->
    root.children = values.map { value -> Node(value, null, root) }
}
