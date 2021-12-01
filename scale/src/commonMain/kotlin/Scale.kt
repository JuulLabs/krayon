package com.juul.krayon.scale

/**
 * Maps values from a [D]omain to a [R]ange.
 *
 * Generally, this is used to map from a raw data format to a visual representation, like a color or position location.
 */
public interface Scale<D, R> {
    public fun scale(input: D): R
}
