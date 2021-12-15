package com.juul.krayon.hierarchy

public class Node<T> internal constructor(
    /** Data associated with this node. */
    public val data: T,

    /** The parent node, if any. */
    public val parent: Node<T>? = null,
) {

    /** Child nodes. If this is a leaf, then this list is empty. */
    public var children: List<Node<T>> = emptyList()
        internal set

    /** Weight associated with this item. Note that this defaults to 0, and must be explicitly set via [sum] or [count]. */
    @PublishedApi
    internal var weight: Double = 0.0
}

/** Number of parents before hitting the root [Node]. If this is the root, then this is 0. */
public val <T> Node<T>.depth: Int
    get() = if (parent == null) 0 else parent.depth + 1

/** Maximum number of children before hitting a leaf [Node]. If this is a leaf, then this is 0. */
public val <T> Node<T>.height: Int
    get() = children.maxOfOrNull { it.height + 1 } ?: 0

/** Returns `true` if this node has no children. */
public val <T> Node<T>.isLeaf: Boolean
    get() = this.children.isEmpty()

/** Returns ancestor nodes, starting with `this` and then following the [Node.parent] chain. */
public fun <T> Node<T>.ancestors(): Sequence<Node<T>> = sequence {
    var current: Node<T>? = this@ancestors
    while (current != null) {
        yield(current)
        current = parent
    }
}

/** Returns descendant nodes, starting with `this` and then following the [Node.children] chain in a breadth-first traversal. */
public fun <T> Node<T>.traverseBreadthFirst(): Sequence<Node<T>> =
    sequence {
        val remaining = ArrayDeque(listOf(this@traverseBreadthFirst))
        while (remaining.isNotEmpty()) {
            val current = remaining.removeFirst()
            yield(current)
            current.children.forEach(remaining::addLast)
        }
    }

/** Returns descendant nodes, starting with `this` and then following the [Node.children] chain in a depth-first traversal. */
public fun <T> Node<T>.traversePreOrder(): Sequence<Node<T>> =
    sequence {
        yield(this@traversePreOrder)
        for (child in children) {
            yieldAll(child.traversePreOrder())
        }
    }

/** Returns descendant nodes, starting with the [Node.children] chain in a depth-first traversal, and ending with `this`. */
public fun <T> Node<T>.traversePostOrder(): Sequence<Node<T>> =
    sequence {
        for (child in children) {
            yieldAll(child.traversePostOrder())
        }
        yield(this@traversePostOrder)
    }

public inline fun <T> Node<T>.sum(
    crossinline value: (T) -> Double,
): Node<T> = eachAfter { node -> node.weight = value(node.data) + children.sumOf { it.weight } }

public fun <T> Node<T>.count(): Node<T> = sum { 1.0 }

public fun <T: Any> Node<T>.sort(
    comparator: Comparator<T>
): Node<T> = eachBefore { node ->
    node.children = node.children.sortedWith { left, right ->
        comparator.compare(left.data, right.data)
    }
}

public inline fun <T> Node<T>.each(
    crossinline action: (Node<T>) -> Unit,
): Node<T> = apply { traverseBreadthFirst().forEach(action) }

public inline fun <T> Node<T>.eachAfter(
    crossinline action: (Node<T>) -> Unit,
): Node<T> = apply { traversePostOrder().forEach(action) }

public inline fun <T> Node<T>.eachBefore(
    crossinline action: (Node<T>) -> Unit,
): Node<T> = apply { traversePreOrder().forEach(action) }

public inline fun <T> Node<T>.eachIndexed(
    crossinline action: (Int, Node<T>) -> Unit,
): Node<T> = apply { traverseBreadthFirst().forEachIndexed(action) }

public inline fun <T> Node<T>.eachAfterIndexed(
    crossinline action: (Int, Node<T>) -> Unit,
): Node<T> = apply { traversePostOrder().forEachIndexed(action) }

public inline fun <T> Node<T>.eachBeforeIndexed(
    crossinline action: (Int, Node<T>) -> Unit,
): Node<T> = apply { traversePreOrder().forEachIndexed(action) }
