package com.juul.krayon.scale

public inline fun <I, O : Comparable<O>> Iterable<I>.max(
    crossinline selector: (I) -> O?,
): O {
    var max: O? = null
    for (input in this) {
        val output = selector(input) ?: continue
        if (max == null || max < output) max = output
    }
    return checkNotNull(max)
}
