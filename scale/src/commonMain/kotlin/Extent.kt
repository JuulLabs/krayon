package com.juul.krayon.scale

public inline fun <I, O : Comparable<O>> List<I>.extent(
    crossinline selector: (I) -> O?,
): List<O> {
    // TODO: make this more efficient even though it'll be more lines
    val buffer = mapNotNull(selector)
    return listOf(buffer.minOf { it }, buffer.maxOf { it })
}
