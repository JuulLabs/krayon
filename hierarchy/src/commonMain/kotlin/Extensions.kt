package com.juul.krayon.hierarchy

/** Fills weird gap in standard library. Copy-paste of `sumOf` but all [Double] changed to [Float]. */
@PublishedApi
internal inline fun <T> Iterable<T>.sumOf(selector: (T) -> Float): Float {
    var sum = 0f
    for (element in this) {
        sum += selector(element)
    }
    return sum
}
