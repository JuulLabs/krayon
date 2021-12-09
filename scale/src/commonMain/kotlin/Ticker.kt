package com.juul.krayon.scale

public interface Ticker<T> {
    public fun ticks(start: T, stop: T, count: Int): List<T>
}
