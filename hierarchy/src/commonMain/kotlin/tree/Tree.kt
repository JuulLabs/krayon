package com.juul.krayon.hierarchy.tree

import com.juul.krayon.hierarchy.Node
import com.juul.krayon.hierarchy.Point
import com.juul.krayon.hierarchy.depth
import com.juul.krayon.hierarchy.traversePreOrder

/** Applies [tree] to `this`, attaching a [Point] layout to every node. */
public fun <T> Node<T, *>.layoutWith(tree: Tree<T>): Node<T, Point> = tree.layout(this)

/**
 * Node-link tree diagram using the Reingold–Tilford "tidy" algorithm, as improved by Buchheim et al.
 *
 * When [nodeSize] is `false`, [width] and [height] are the extent of the layout; otherwise they are the
 * per-node spacing along the x and y axes.
 */
public class Tree<T>(
    public var width: Float = 1f,
    public var height: Float = 1f,
    public var nodeSize: Boolean = false,
    public var separation: (Node<T, Point>, Node<T, Point>) -> Float = defaultSeparation(),
) {
    public fun layout(root: Node<T, *>): Node<T, Point> {
        @Suppress("UNCHECKED_CAST")
        val typedRoot = root as Node<T, Point>
        val x = HashMap<Node<T, Point>, Float>()

        val t = wrapTree(typedRoot)
        t.eachAfter(::firstWalk)
        checkNotNull(t.parent).mod = -t.prelim
        t.eachBefore { v -> secondWalk(v, x) }

        if (nodeSize) {
            typedRoot.traversePreOrder().forEach { node ->
                node.layout = Point(x.getValue(node) * width, node.depth * height)
            }
        } else {
            var left = typedRoot
            var right = typedRoot
            var bottom = typedRoot
            typedRoot.traversePreOrder().forEach { node ->
                if (x.getValue(node) < x.getValue(left)) left = node
                if (x.getValue(node) > x.getValue(right)) right = node
                if (node.depth > bottom.depth) bottom = node
            }
            val s = if (left === right) 1f else separation(left, right) / 2f
            val tx = s - x.getValue(left)
            val kx = width / (x.getValue(right) + s + tx)
            val ky = height / (if (bottom.depth != 0) bottom.depth.toFloat() else 1f)
            typedRoot.traversePreOrder().forEach { node ->
                node.layout = Point((x.getValue(node) + tx) * kx, node.depth * ky)
            }
        }
        return typedRoot
    }

    private fun nextLeft(v: TreeNode<T>): TreeNode<T>? = v.children?.first() ?: v.thread

    private fun nextRight(v: TreeNode<T>): TreeNode<T>? = v.children?.last() ?: v.thread

    private fun moveSubtree(wm: TreeNode<T>, wp: TreeNode<T>, shift: Float) {
        val change = shift / (wp.number - wm.number)
        wp.change -= change
        wp.shift += shift
        wm.change += change
        wp.prelim += shift
        wp.mod += shift
    }

    private fun executeShifts(v: TreeNode<T>) {
        val children = v.children ?: return
        var shift = 0f
        var change = 0f
        for (i in children.indices.reversed()) {
            val w = children[i]
            w.prelim += shift
            w.mod += shift
            change += w.change
            shift += w.shift + change
        }
    }

    private fun nextAncestor(vim: TreeNode<T>, v: TreeNode<T>, ancestor: TreeNode<T>): TreeNode<T> =
        if (vim.ancestor.parent === v.parent) vim.ancestor else ancestor

    private fun apportion(v: TreeNode<T>, w: TreeNode<T>?, ancestorIn: TreeNode<T>): TreeNode<T> {
        var ancestor = ancestorIn
        if (w == null) return ancestor

        var vip = v
        var vop = v
        var vim = w
        var vom = checkNotNull(vip.parent).children!!.first()
        var sip = vip.mod
        var sop = vop.mod
        var sim = vim.mod
        var som = vom.mod

        var nextVim = nextRight(vim)
        var nextVip = nextLeft(vip)
        while (nextVim != null && nextVip != null) {
            vim = nextVim
            vip = nextVip
            vom = checkNotNull(nextLeft(vom))
            vop = checkNotNull(nextRight(vop))
            vop.ancestor = v
            val shift = (vim.prelim + sim) - (vip.prelim + sip) + separation(vim.node, vip.node)
            if (shift > 0f) {
                moveSubtree(nextAncestor(vim, v, ancestor), v, shift)
                sip += shift
                sop += shift
            }
            sim += vim.mod
            sip += vip.mod
            som += vom.mod
            sop += vop.mod
            nextVim = nextRight(vim)
            nextVip = nextLeft(vip)
        }
        if (nextVim != null && nextRight(vop) == null) {
            vop.thread = nextVim
            vop.mod += sim - sop
        }
        if (nextVip != null && nextLeft(vom) == null) {
            vom.thread = nextVip
            vom.mod += sip - som
            ancestor = v
        }
        return ancestor
    }

    private fun firstWalk(v: TreeNode<T>) {
        val children = v.children
        val siblings = checkNotNull(v.parent).children!!
        val w = if (v.number != 0) siblings[v.number - 1] else null
        if (children != null) {
            executeShifts(v)
            val midpoint = (children.first().prelim + children.last().prelim) / 2f
            if (w != null) {
                v.prelim = w.prelim + separation(v.node, w.node)
                v.mod = v.prelim - midpoint
            } else {
                v.prelim = midpoint
            }
        } else if (w != null) {
            v.prelim = w.prelim + separation(v.node, w.node)
        }
        val parent = checkNotNull(v.parent)
        parent.ancestor2 = apportion(v, w, parent.ancestor2 ?: siblings.first())
    }

    private fun secondWalk(v: TreeNode<T>, x: HashMap<Node<T, Point>, Float>) {
        val parent = checkNotNull(v.parent)
        x[v.node] = v.prelim + parent.mod
        v.mod += parent.mod
    }

    private fun wrapTree(root: Node<T, Point>): TreeNode<T> {
        fun wrap(node: Node<T, Point>, index: Int, parent: TreeNode<T>?): TreeNode<T> {
            val wrapped = treeNode(node, index)
            wrapped.parent = parent
            val children = node.children
            if (children.isNotEmpty()) {
                wrapped.children = children.mapIndexed { i, child -> wrap(child, i, wrapped) }
            }
            return wrapped
        }
        val wrapped = wrap(root, 0, null)
        val superRoot = TreeNode<T>(null, 0)
        superRoot.ancestor = superRoot
        superRoot.children = listOf(wrapped)
        wrapped.parent = superRoot
        return wrapped
    }
}

/** Default node separation: `1` between siblings, `2` between nodes with different parents. */
public fun <T> defaultSeparation(): (Node<T, Point>, Node<T, Point>) -> Float =
    { a, b -> if (a.parent === b.parent) 1f else 2f }

private class TreeNode<T>(
    node: Node<T, Point>?,
    val number: Int,
) {
    private val backing: Node<T, Point>? = node

    /** The wrapped hierarchy node. Never accessed for the synthetic super-root. */
    val node: Node<T, Point> get() = checkNotNull(backing)

    var parent: TreeNode<T>? = null
    var children: List<TreeNode<T>>? = null

    /** Default ancestor (`A` in the paper). */
    var ancestor2: TreeNode<T>? = null

    /** Ancestor (`a` in the paper); defaults to itself. */
    lateinit var ancestor: TreeNode<T>

    var prelim: Float = 0f
    var mod: Float = 0f
    var change: Float = 0f
    var shift: Float = 0f
    var thread: TreeNode<T>? = null
}

private fun <T> treeNode(node: Node<T, Point>?, index: Int): TreeNode<T> =
    TreeNode(node, index).apply { ancestor = this }

private fun <T> TreeNode<T>.eachAfter(action: (TreeNode<T>) -> Unit) {
    children?.forEach { it.eachAfter(action) }
    action(this)
}

private fun <T> TreeNode<T>.eachBefore(action: (TreeNode<T>) -> Unit) {
    action(this)
    children?.forEach { it.eachBefore(action) }
}
