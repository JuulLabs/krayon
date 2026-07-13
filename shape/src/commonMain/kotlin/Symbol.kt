package com.juul.krayon.shape

import com.juul.krayon.kanvas.Path

public fun <D : Any> symbol(): Symbol<D> = Symbol()

/**
 * Renders a [SymbolType] to a [Path], like [`d3.symbol`](https://github.com/d3/d3-shape/blob/main/src/symbol.js).
 * Both the symbol [type] and its [size] (area in square pixels, default `64`) may be constants or accessor functions.
 */
public class Symbol<D : Any> internal constructor() {

    private var type: (Arguments<D>) -> SymbolType = { Circle }
    private var size: (Arguments<D>) -> Float = { 64f }

    public fun type(type: SymbolType): Symbol<D> = apply { this.type = { type } }

    public fun type(type: (Arguments<D>) -> SymbolType): Symbol<D> = apply { this.type = type }

    public fun size(size: Float): Symbol<D> = apply { this.size = { size } }

    public fun size(size: (Arguments<D>) -> Float): Symbol<D> = apply { this.size = size }

    public fun render(datum: D, index: Int = 0, data: List<D?> = listOf(datum)): Path = Path {
        val arguments = Arguments(datum, index, data)
        type(arguments).draw(this, size(arguments))
    }
}
