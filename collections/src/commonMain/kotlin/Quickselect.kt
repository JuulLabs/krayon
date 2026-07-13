package com.juul.krayon.collections

import kotlin.math.exp
import kotlin.math.ln
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sign
import kotlin.math.sqrt

/**
 * Rearranges [list] in place so that the element at index [k] is the element that would be there if
 * the range `[from, to]` were fully sorted by [comparator]; all smaller elements precede it and all
 * larger elements follow. Based on
 * [Vladimir Agafonkin's quickselect](https://github.com/mourner/quickselect) (ISC License), as used
 * by [d3](https://github.com/d3/d3-array#quickselect).
 */
public fun <T> MutableList<T>.quickselect(
    k: Int,
    from: Int = 0,
    to: Int = size - 1,
    comparator: Comparator<in T>,
): MutableList<T> {
    var left = max(0, from)
    var right = min(size - 1, to)
    if (k !in left..right) return this

    while (right > left) {
        if (right - left > 600) {
            val n = right - left + 1
            val m = k - left + 1
            val z = ln(n.toDouble())
            val s = 0.5 * exp(2 * z / 3)
            val sd = 0.5 * sqrt(z * s * (n - s) / n) * sign((m - n / 2.0))
            val newLeft = max(left, (k - m * s / n + sd).toInt())
            val newRight = min(right, (k + (n - m) * s / n + sd).toInt())
            quickselect(k, newLeft, newRight, comparator)
        }

        val t = this[k]
        var i = left
        var j = right

        swap(left, k)
        if (comparator.compare(this[right], t) > 0) swap(left, right)

        while (i < j) {
            swap(i, j)
            i++
            j--
            while (comparator.compare(this[i], t) < 0) i++
            while (comparator.compare(this[j], t) > 0) j--
        }

        if (comparator.compare(this[left], t) == 0) {
            swap(left, j)
        } else {
            j++
            swap(j, right)
        }

        if (j <= k) left = j + 1
        if (k <= j) right = j - 1
    }
    return this
}

private fun <T> MutableList<T>.swap(i: Int, j: Int) {
    val t = this[i]
    this[i] = this[j]
    this[j] = t
}
