package com.juul.krayon.selection

import com.juul.krayon.element.Element

public inline fun <E1: Element, E2: Element, D> Selection<E1, D>.append(
    crossinline value: E1.(datum: D, index: Int, group: Group<E1, D>) -> E2,
): Selection<E2, D> = select { datum, index, group -> appendChild(value(datum, index, group)) }
