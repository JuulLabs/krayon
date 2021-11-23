package com.juul.krayon.selection

import com.juul.krayon.element.Element

public inline fun <T> Selection<T>.append(
    crossinline value: Element.(datum: T, index: Int, group: Group<T>) -> Element,
): Selection<T> = select { datum, index, group -> appendChild(value(datum, index, group)) }
