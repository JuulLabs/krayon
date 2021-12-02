package com.juul.krayon.scale

/**
 * Maps values from a [D]omain to a [R]ange.
 *
 * Generally, this is used to map from a raw data format (a time, a measurement, etc) to a visual representation (a color, a coordinate, etc).
 */
public interface Scale<D, R> {
    public fun scale(input: D): R
}
