package com.juul.krayon.time

/**
 * Converts this [Long] to an [Int].
 *
 * Unlike [toInt], input values not representable by an [Int] are coerced
 * to either [Int.MIN_VALUE] or [Int.MAX_VALUE], whichever is closer.
 */
internal fun Long.coerceToInt(): Int =
    this.coerceIn(Int.MIN_VALUE.toLong(), Int.MAX_VALUE.toLong()).toInt()
