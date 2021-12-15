package com.juul.krayon.hierarchy

public fun <T> hierarchy(
    root: T,
    getChildren: (T) -> List<T>,
): Node<T> {
    fun addChildren(parentNode: Node<T>) {
        val children = getChildren(parentNode.data)
        parentNode.children = children.map { child ->
            Node(child, parentNode).also { childNode ->
                addChildren(childNode)
            }
        }
    }
    return Node(root).also { addChildren(it) }
}
