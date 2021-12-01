package com.juul.krayon.interpolate

public interface Inverter<T> {
    public fun invert(value: T): Float
}
