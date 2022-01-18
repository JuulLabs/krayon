package com.juul.krayon.scale

public inline fun <I, O : Comparable<O>> Iterable<I>.extent(
    crossinline selector: (I) -> O?,
): Iterable<O> {
    var min: O? = null
    var max: O? = null
    for (input in this) {
        val output = selector(input) ?: continue
        if (min == null || min > output) min = output
        if (max == null || max < output) max = output
    }
    return listOf(checkNotNull(min), checkNotNull(max))
}
