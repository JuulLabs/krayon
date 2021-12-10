package com.juul.krayon.scale

public fun interface Ticker<T> {
    public fun ticks(start: T, stop: T, count: Int): List<T>
}
