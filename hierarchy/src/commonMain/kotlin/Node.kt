package com.juul.krayon.hierarchy

public class Node<T, L> internal constructor(
    /** Data associated with this node. */
    public val data: T,

    layout: L,

    /** The parent node, if any. */
    public val parent: Node<T, L>? = null,
) {

    /** Layout value. Null when initially created, but can be changed by using a tree-map or similar.  */
    public var layout: L = layout
        internal set

    /** Child nodes. If this is a leaf, then this list is empty. */
    public var children: List<Node<T, L>> = emptyList()
        internal set

    /** Weight associated with this item. Note that this defaults to 0, and must be explicitly set via [sum] or [count]. */
    @PublishedApi
    internal var weight: Float = 0f
}

/** Number of parents before hitting the root [Node]. If this is the root, then this is 0. */
public val <T, L> Node<T, L>.depth: Int
    get() = if (parent == null) 0 else parent.depth + 1

/** Maximum number of children before hitting a leaf [Node]. If this is a leaf, then this is 0. */
public val <T, L> Node<T, L>.height: Int
    get() = children.maxOfOrNull { it.height + 1 } ?: 0

/** Returns `true` if this node has no children. */
public val <T, L> Node<T, L>.isLeaf: Boolean
    get() = children.isEmpty()

/** Returns ancestor nodes, starting with `this` and then following the [Node.parent] chain. */
public fun <T, L> Node<T, L>.ancestors(): Sequence<Node<T, L>> = sequence {
    var current: Node<T, L>? = this@ancestors
    while (current != null) {
        yield(current)
        current = current.parent
    }
}

/** Returns descendant nodes, starting with `this` and then following the [Node.children] chain in a breadth-first traversal. */
public fun <T, L> Node<T, L>.traverseBreadthFirst(): Sequence<Node<T, L>> =
    sequence {
        val remaining = ArrayDeque(listOf(this@traverseBreadthFirst))
        while (remaining.isNotEmpty()) {
            val current = remaining.removeFirst()
            yield(current)
            current.children.forEach(remaining::addLast)
        }
    }

/** Returns descendant nodes, starting with `this` and then following the [Node.children] chain in a depth-first traversal. */
public fun <T, L> Node<T, L>.traversePreOrder(): Sequence<Node<T, L>> =
    sequence {
        yield(this@traversePreOrder)
        for (child in children) {
            yieldAll(child.traversePreOrder())
        }
    }

/** Returns descendant nodes, starting with the [Node.children] chain in a depth-first traversal, and ending with `this`. */
public fun <T, L> Node<T, L>.traversePostOrder(): Sequence<Node<T, L>> =
    sequence {
        for (child in children) {
            yieldAll(child.traversePostOrder())
        }
        yield(this@traversePostOrder)
    }

public inline fun <T, L> Node<T, L>.sum(
    crossinline value: (T) -> Float,
): Node<T, L> = eachAfter { node -> node.weight = value(node.data) + node.children.sumOf { it.weight } }

public fun <T, L> Node<T, L>.count(): Node<T, L> = sum { 1f }

public fun <T, L> Node<T, L>.sort(
    comparator: Comparator<T>,
): Node<T, L> = eachBefore { node ->
    node.children = node.children.sortedWith { left, right ->
        comparator.compare(left.data, right.data)
    }
}

public inline fun <T, L> Node<T, L>.each(
    crossinline action: (Node<T, L>) -> Unit,
): Node<T, L> = apply { traverseBreadthFirst().forEach(action) }

public inline fun <T, L> Node<T, L>.eachAfter(
    crossinline action: (Node<T, L>) -> Unit,
): Node<T, L> = apply { traversePostOrder().forEach(action) }

public inline fun <T, L> Node<T, L>.eachBefore(
    crossinline action: (Node<T, L>) -> Unit,
): Node<T, L> = apply { traversePreOrder().forEach(action) }

public inline fun <T, L> Node<T, L>.eachIndexed(
    crossinline action: (Int, Node<T, L>) -> Unit,
): Node<T, L> = apply { traverseBreadthFirst().forEachIndexed(action) }

public inline fun <T, L> Node<T, L>.eachAfterIndexed(
    crossinline action: (Int, Node<T, L>) -> Unit,
): Node<T, L> = apply { traversePostOrder().forEachIndexed(action) }

public inline fun <T, L> Node<T, L>.eachBeforeIndexed(
    crossinline action: (Int, Node<T, L>) -> Unit,
): Node<T, L> = apply { traversePreOrder().forEachIndexed(action) }

/**
 * Returns a breadth-first traversal of nodes, removing those with null data, and pairing the data to the layout.
 *
 * Especially useful with [flatHierarchy] after performing a layout, before feeding into selection data.
 */
public fun <T, L> Node<T?, L>.removeHierarchy(): Sequence<Pair<T, L>> =
    traverseBreadthFirst()
        .filter { it.data != null }
        .map { checkNotNull(it.data) to it.layout }

/** A directed edge from a parent [source] node to a child [target] node, as returned by [links]. */
public class Link<T, L> internal constructor(
    public val source: Node<T, L>,
    public val target: Node<T, L>,
)

/** Returns all descendant nodes in breadth-first order, starting with `this`. */
public fun <T, L> Node<T, L>.descendants(): List<Node<T, L>> = traverseBreadthFirst().toList()

/** Returns all leaf nodes (those with no children) in pre-order. */
public fun <T, L> Node<T, L>.leaves(): List<Node<T, L>> = traversePreOrder().filter { it.isLeaf }.toList()

/** Returns a [Link] from each node's parent to that node, for every descendant except `this`, in breadth-first order. */
public fun <T, L> Node<T, L>.links(): List<Link<T, L>> {
    val root = this
    val result = mutableListOf<Link<T, L>>()
    traverseBreadthFirst().forEach { node ->
        if (node !== root) result += Link(checkNotNull(node.parent), node)
    }
    return result
}

/** Returns the shortest path of nodes from `this` to [target], passing through their least common ancestor. */
public fun <T, L> Node<T, L>.path(target: Node<T, L>): List<Node<T, L>> {
    val ancestor = leastCommonAncestor(this, target)
    val nodes = mutableListOf(this)
    var start: Node<T, L> = this
    while (start !== ancestor) {
        start = checkNotNull(start.parent)
        nodes.add(start)
    }
    val split = nodes.size
    var end: Node<T, L> = target
    while (end !== ancestor) {
        nodes.add(split, end)
        end = checkNotNull(end.parent)
    }
    return nodes
}

private fun <T, L> leastCommonAncestor(a: Node<T, L>, b: Node<T, L>): Node<T, L>? {
    if (a === b) return a
    val aNodes = a.ancestors().toMutableList()
    val bNodes = b.ancestors().toMutableList()
    var common: Node<T, L>? = null
    var x = aNodes.removeLastOrNull()
    var y = bNodes.removeLastOrNull()
    while (x === y && x != null) {
        common = x
        x = aNodes.removeLastOrNull()
        y = bNodes.removeLastOrNull()
    }
    return common
}

/** Returns a deep copy of the subtree rooted at `this`, detached from any parent. Data and layout references are shared. */
public fun <T, L> Node<T, L>.copy(): Node<T, L> {
    fun copyNode(source: Node<T, L>, parent: Node<T, L>?): Node<T, L> {
        val node = Node(source.data, source.layout, parent)
        node.weight = source.weight
        node.children = source.children.map { copyNode(it, node) }
        return node
    }
    return copyNode(this, null)
}
