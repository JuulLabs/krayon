package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.ElementBuilder

public fun <E1 : Element, E2 : Element, D> Selection<E1, D>.append(
    builder: ElementBuilder<E2>,
): Selection<E2, D> = select { appendChild(builder.build()) }

public inline fun <E1 : Element, E2 : Element, D> Selection<E1, D>.append(
    crossinline value: E1.(Arguments<E1, D>) -> E2,
): Selection<E2, D> = select { args -> appendChild(value(args)) }
