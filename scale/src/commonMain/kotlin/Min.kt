package com.juul.krayon.scale

public inline fun <I, O : Comparable<O>> Iterable<I>.min(
    crossinline selector: (I) -> O?,
): O {
    var min: O? = null
    for (input in this) {
        val output = selector(input) ?: continue
        if (min == null || min > output) min = output
    }
    return checkNotNull(min)
}
