package com.juul.krayon.hierarchy.pack

import com.juul.krayon.hierarchy.Node
import com.juul.krayon.hierarchy.isLeaf
import com.juul.krayon.hierarchy.traversePostOrder
import com.juul.krayon.hierarchy.traversePreOrder
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

/** Circle attached as a layout by [Pack]. */
public class Circle internal constructor(
    public val x: Float,
    public val y: Float,
    public val radius: Float,
) {
    override fun toString(): String = "Circle(x=$x, y=$y, radius=$radius)"
}

/** Applies [pack] to `this`, attaching a [Circle] layout to every node. */
public fun <T> Node<T, *>.layoutWith(pack: Pack<T>): Node<T, Circle> = pack.layout(this)

/**
 * Circle-packing layout. Leaves are sized by [radius] (defaulting to the square root of the node weight set via
 * `sum`/`count`), packed among siblings, and enclosed within their parent. Results are scaled to fit [width] by
 * [height]. [padding] separates adjacent circles.
 */
public class Pack<T>(
    public var width: Float = 1f,
    public var height: Float = 1f,
    public var radius: ((Node<T, Circle>) -> Float)? = null,
    public var padding: (Node<T, Circle>) -> Float = { 0f },
) {
    public fun layout(root: Node<T, *>): Node<T, Circle> {
        @Suppress("UNCHECKED_CAST")
        val typedRoot = root as Node<T, Circle>
        val circles = HashMap<Node<T, Circle>, PackCircle>()

        fun circleOf(node: Node<T, Circle>) = circles.getOrPut(node) { PackCircle() }

        val random = lcg()
        circleOf(typedRoot).apply {
            x = width / 2.0
            y = height / 2.0
        }

        val radiusFn = radius
        if (radiusFn != null) {
            typedRoot.traversePreOrder().forEach { node ->
                if (node.isLeaf) circleOf(node).r = max(0.0, radiusFn(node).toDouble())
            }
            typedRoot.traversePostOrder().forEach { node -> packChildren(node, padding, 0.5, random, ::circleOf) }
            typedRoot.traversePreOrder().forEach { node -> translateChild(node, 1.0, ::circleOf) }
        } else {
            typedRoot.traversePreOrder().forEach { node ->
                if (node.isLeaf) circleOf(node).r = max(0.0, sqrt(node.weight.toDouble()))
            }
            typedRoot.traversePostOrder().forEach { node -> packChildren(node, { 0f }, 1.0, random, ::circleOf) }
            val k = circleOf(typedRoot).r / min(width, height)
            typedRoot.traversePostOrder().forEach { node -> packChildren(node, padding, k, random, ::circleOf) }
            val translation = min(width, height) / (2.0 * circleOf(typedRoot).r)
            typedRoot.traversePreOrder().forEach { node -> translateChild(node, translation, ::circleOf) }
        }

        typedRoot.traversePreOrder().forEach { node ->
            val circle = circleOf(node)
            node.layout = Circle(circle.x.toFloat(), circle.y.toFloat(), circle.r.toFloat())
        }
        return typedRoot
    }

    private fun packChildren(
        node: Node<T, Circle>,
        padding: (Node<T, Circle>) -> Float,
        k: Double,
        random: () -> Double,
        circleOf: (Node<T, Circle>) -> PackCircle,
    ) {
        val children = node.children
        if (children.isEmpty()) return
        val r = (padding(node) * k).let { if (it.isNaN()) 0.0 else it }
        val childCircles = children.map(circleOf)
        if (r != 0.0) childCircles.forEach { it.r += r }
        val enclosingRadius = packSiblingsRandom(childCircles, random)
        if (r != 0.0) childCircles.forEach { it.r -= r }
        circleOf(node).r = enclosingRadius + r
    }

    private fun translateChild(
        node: Node<T, Circle>,
        k: Double,
        circleOf: (Node<T, Circle>) -> PackCircle,
    ) {
        val circle = circleOf(node)
        circle.r *= k
        val parent = node.parent
        if (parent != null) {
            val parentCircle = circleOf(parent)
            circle.x = parentCircle.x + k * circle.x
            circle.y = parentCircle.y + k * circle.y
        }
    }
}

internal class PackCircle {
    var x: Double = 0.0
    var y: Double = 0.0
    var r: Double = 0.0
}

private class ChainNode(val circle: PackCircle) {
    var next: ChainNode? = null
    var previous: ChainNode? = null
}

private fun place(b: PackCircle, a: PackCircle, c: PackCircle) {
    val dx = b.x - a.x
    val dy = b.y - a.y
    val d2 = dx * dx + dy * dy
    if (d2 != 0.0) {
        val a2 = (a.r + c.r).let { it * it }
        val b2 = (b.r + c.r).let { it * it }
        if (a2 > b2) {
            val x = (d2 + b2 - a2) / (2.0 * d2)
            val y = sqrt(max(0.0, b2 / d2 - x * x))
            c.x = b.x - x * dx - y * dy
            c.y = b.y - x * dy + y * dx
        } else {
            val x = (d2 + a2 - b2) / (2.0 * d2)
            val y = sqrt(max(0.0, a2 / d2 - x * x))
            c.x = a.x + x * dx - y * dy
            c.y = a.y + x * dy + y * dx
        }
    } else {
        c.x = a.x + c.r
        c.y = a.y
    }
}

private fun intersects(a: PackCircle, b: PackCircle): Boolean {
    val dr = a.r + b.r - 1e-6
    val dx = b.x - a.x
    val dy = b.y - a.y
    return dr > 0.0 && dr * dr > dx * dx + dy * dy
}

private fun score(node: ChainNode): Double {
    val a = node.circle
    val b = checkNotNull(node.next).circle
    val ab = a.r + b.r
    val dx = (a.x * b.r + b.x * a.r) / ab
    val dy = (a.y * b.r + b.y * a.r) / ab
    return dx * dx + dy * dy
}

internal fun packSiblingsRandom(circles: List<PackCircle>, random: () -> Double): Double {
    val n = circles.size
    if (n == 0) return 0.0

    val first = circles[0]
    first.x = 0.0
    first.y = 0.0
    if (n == 1) return first.r

    val second = circles[1]
    first.x = -second.r
    second.x = first.r
    second.y = 0.0
    if (n == 2) return first.r + second.r

    place(second, first, circles[2])

    var a = ChainNode(first)
    var b = ChainNode(second)
    var c = ChainNode(circles[2])
    a.next = b
    c.previous = b
    b.next = c
    a.previous = c
    c.next = a
    b.previous = a

    var i = 3
    while (i < n) {
        place(a.circle, b.circle, circles[i])
        c = ChainNode(circles[i])

        var j = checkNotNull(b.next)
        var k = checkNotNull(a.previous)
        var sj = b.circle.r
        var sk = a.circle.r
        var collided = false
        do {
            if (sj <= sk) {
                if (intersects(j.circle, c.circle)) {
                    b = j
                    a.next = b
                    b.previous = a
                    collided = true
                    break
                }
                sj += j.circle.r
                j = checkNotNull(j.next)
            } else {
                if (intersects(k.circle, c.circle)) {
                    a = k
                    a.next = b
                    b.previous = a
                    collided = true
                    break
                }
                sk += k.circle.r
                k = checkNotNull(k.previous)
            }
        } while (j !== k.next)

        if (collided) continue

        val oldB = b
        c.previous = a
        c.next = oldB
        a.next = c
        oldB.previous = c
        b = c

        var aa = score(a)
        var scan = c
        while (true) {
            scan = checkNotNull(scan.next)
            if (scan === b) break
            val ca = score(scan)
            if (ca < aa) {
                a = scan
                aa = ca
            }
        }
        b = checkNotNull(a.next)
        i++
    }

    val chain = mutableListOf(b.circle)
    var scan = checkNotNull(b.next)
    while (scan !== b) {
        chain.add(scan.circle)
        scan = checkNotNull(scan.next)
    }
    val enclosing = packEncloseRandom(chain, random)
    for (circle in circles) {
        circle.x -= enclosing.x
        circle.y -= enclosing.y
    }
    return enclosing.r
}

private fun packEncloseRandom(circles: List<PackCircle>, random: () -> Double): PackCircle {
    val shuffled = circles.toMutableList()
    shuffle(shuffled, random)
    val n = shuffled.size
    var i = 0
    var basis = emptyList<PackCircle>()
    var enclosing: PackCircle? = null
    while (i < n) {
        val p = shuffled[i]
        val e = enclosing
        if (e != null && enclosesWeak(e, p)) {
            i++
        } else {
            basis = extendBasis(basis, p)
            enclosing = encloseBasis(basis)
            i = 0
        }
    }
    return checkNotNull(enclosing)
}

private fun shuffle(array: MutableList<PackCircle>, random: () -> Double) {
    var m = array.size
    while (m != 0) {
        val i = (random() * m).toInt()
        m -= 1
        val t = array[m]
        array[m] = array[i]
        array[i] = t
    }
}

private fun extendBasis(basis: List<PackCircle>, p: PackCircle): List<PackCircle> {
    if (enclosesWeakAll(p, basis)) return listOf(p)

    for (i in basis.indices) {
        if (enclosesNot(p, basis[i]) && enclosesWeakAll(encloseBasis2(basis[i], p), basis)) {
            return listOf(basis[i], p)
        }
    }

    for (i in 0 until basis.size - 1) {
        for (j in i + 1 until basis.size) {
            if (enclosesNot(encloseBasis2(basis[i], basis[j]), p) &&
                enclosesNot(encloseBasis2(basis[i], p), basis[j]) &&
                enclosesNot(encloseBasis2(basis[j], p), basis[i]) &&
                enclosesWeakAll(encloseBasis3(basis[i], basis[j], p), basis)
            ) {
                return listOf(basis[i], basis[j], p)
            }
        }
    }

    error("Unable to extend enclosing basis")
}

private fun enclosesNot(a: PackCircle, b: PackCircle): Boolean {
    val dr = a.r - b.r
    val dx = b.x - a.x
    val dy = b.y - a.y
    return dr < 0.0 || dr * dr < dx * dx + dy * dy
}

private fun enclosesWeak(a: PackCircle, b: PackCircle): Boolean {
    val dr = a.r - b.r + max(max(a.r, b.r), 1.0) * 1e-9
    val dx = b.x - a.x
    val dy = b.y - a.y
    return dr > 0.0 && dr * dr > dx * dx + dy * dy
}

private fun enclosesWeakAll(a: PackCircle, basis: List<PackCircle>): Boolean =
    basis.all { enclosesWeak(a, it) }

private fun encloseBasis(basis: List<PackCircle>): PackCircle = when (basis.size) {
    1 -> encloseBasis1(basis[0])
    2 -> encloseBasis2(basis[0], basis[1])
    else -> encloseBasis3(basis[0], basis[1], basis[2])
}

private fun encloseBasis1(a: PackCircle): PackCircle = PackCircle().apply {
    x = a.x
    y = a.y
    r = a.r
}

private fun encloseBasis2(a: PackCircle, b: PackCircle): PackCircle {
    val x1 = a.x
    val y1 = a.y
    val r1 = a.r
    val x2 = b.x
    val y2 = b.y
    val r2 = b.r
    val x21 = x2 - x1
    val y21 = y2 - y1
    val r21 = r2 - r1
    val l = sqrt(x21 * x21 + y21 * y21)
    return PackCircle().apply {
        x = (x1 + x2 + x21 / l * r21) / 2.0
        y = (y1 + y2 + y21 / l * r21) / 2.0
        r = (l + r1 + r2) / 2.0
    }
}

private fun encloseBasis3(a: PackCircle, b: PackCircle, c: PackCircle): PackCircle {
    val x1 = a.x
    val y1 = a.y
    val r1 = a.r
    val x2 = b.x
    val y2 = b.y
    val r2 = b.r
    val x3 = c.x
    val y3 = c.y
    val r3 = c.r
    val a2 = x1 - x2
    val a3 = x1 - x3
    val b2 = y1 - y2
    val b3 = y1 - y3
    val c2 = r2 - r1
    val c3 = r3 - r1
    val d1 = x1 * x1 + y1 * y1 - r1 * r1
    val d2 = d1 - x2 * x2 - y2 * y2 + r2 * r2
    val d3 = d1 - x3 * x3 - y3 * y3 + r3 * r3
    val ab = a3 * b2 - a2 * b3
    val xa = (b2 * d3 - b3 * d2) / (ab * 2.0) - x1
    val xb = (b3 * c2 - b2 * c3) / ab
    val ya = (a3 * d2 - a2 * d3) / (ab * 2.0) - y1
    val yb = (a2 * c3 - a3 * c2) / ab
    val bigA = xb * xb + yb * yb - 1.0
    val bigB = 2.0 * (r1 + xa * xb + ya * yb)
    val bigC = xa * xa + ya * ya - r1 * r1
    val r = -(if (abs(bigA) > 1e-6) (bigB + sqrt(bigB * bigB - 4.0 * bigA * bigC)) / (2.0 * bigA) else bigC / bigB)
    return PackCircle().apply {
        x = x1 + xa + xb * r
        y = y1 + ya + yb * r
        this.r = r
    }
}

private fun lcg(): () -> Double {
    val a = 1664525.0
    val c = 1013904223.0
    val m = 4294967296.0
    var s = 1.0
    return {
        s = (a * s + c) % m
        s / m
    }
}
