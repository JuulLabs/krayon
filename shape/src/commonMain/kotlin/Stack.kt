package com.juul.krayon.shape

/**
 * A single stacked point, spanning [low] (baseline) to [high] (topline) for its [data] datum.
 * [low] and [high] are mutable so [StackOffset] implementations can reposition the stack.
 */
public class StackPoint<out D> internal constructor(
    public val data: D,
    low: Float,
    high: Float,
) {
    public var low: Float = low

    public var high: Float = high

    override fun toString(): String = "StackPoint(data=$data, low=$low, high=$high)"
}

/** A stacked series for a single [key], usable as a `List` of [StackPoint] for feeding `area()`/`line()`. */
public class Series<out D, out K> internal constructor(
    public val key: K,
    private val points: List<StackPoint<D>>,
) : List<StackPoint<D>> by points {
    public var index: Int = 0
        internal set

    override fun toString(): String = "Series(key=$key, index=$index, points=$points)"
}

public fun <D, K> stack(): Stack<D, K> = Stack()

/**
 * Computes a stacked layout, like [`d3.stack`](https://github.com/d3/d3-shape/blob/main/src/stack.js).
 * Each of the [keys] produces a [Series]; the [value] accessor extracts a numeric value per key/datum.
 */
public class Stack<D, K> internal constructor() {

    private var keys: List<K> = emptyList()
    private var value: (datum: D, key: K) -> Float = defaultValue()
    private var order: StackOrder = StackOrderNone
    private var offset: StackOffset = StackOffsetNone

    public fun keys(keys: List<K>): Stack<D, K> = apply { this.keys = keys.toList() }

    public fun value(value: (datum: D, key: K) -> Float): Stack<D, K> = apply { this.value = value }

    public fun order(order: StackOrder): Stack<D, K> = apply { this.order = order }

    public fun offset(offset: StackOffset): Stack<D, K> = apply { this.offset = offset }

    public operator fun invoke(data: List<D>): List<Series<D, K>> {
        val n = keys.size
        val series = keys.map { key ->
            Series(key, data.map { datum -> StackPoint(datum, low = 0f, high = value(datum, key)) })
        }
        val order = this.order(series)
        for (i in 0 until n) series[order[i]].index = i
        offset(series, order)
        return series
    }
}

private fun <D, K> defaultValue(): (D, K) -> Float = { datum, key ->
    when (datum) {
        is Map<*, *> -> (datum[key] as Number).toFloat()
        is List<*> -> (datum[key as Int] as Number).toFloat()
        else -> error("No default stack value accessor for `$datum`; provide one via `value { datum, key -> ... }`.")
    }
}
