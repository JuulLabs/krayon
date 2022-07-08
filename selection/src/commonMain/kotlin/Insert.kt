package com.juul.krayon.selection

import com.juul.krayon.element.Element
import com.juul.krayon.element.ElementBuilder
import com.juul.krayon.element.ElementSelector

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_insert). */
public fun <E1 : Element, E2 : Element, D> Selection<E1, D>.insert(
    builder: ElementBuilder<E2>,
    before: ElementSelector<*>,
): Selection<E2, D> = select { insertBefore(builder.build(), query(before)) }

/** See analogous [d3 function](https://github.com/d3/d3-selection#selection_insert). */
public inline fun <E1 : Element, E2 : Element, D> Selection<E1, D>.insert(
    crossinline value: E1.(Arguments<D, E1?>) -> E2,
    before: ElementSelector<*>,
): Selection<E2, D> = select { args -> insertBefore(value(args), query(before)) }
