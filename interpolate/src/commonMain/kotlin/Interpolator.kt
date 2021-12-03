package com.juul.krayon.interpolate

public interface Interpolator<T> {
    public fun interpolate(fraction: Float): T
}
