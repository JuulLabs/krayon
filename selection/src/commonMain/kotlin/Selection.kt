package com.juul.krayon.selection

import com.juul.krayon.element.Element

public open class Selection<E : Element, D>(
    public val groups: List<Group<E, D>>,
)

public class UpdateSelection<E : Element, D>(
    groups: List<Group<E, D>>,
    public val enter: EnterSelection<D>,
    public val exit: ExitSelection<E, D>,
) : Selection<E, D>(groups)

public class EnterSelection<D>(
    groups: List<Group<EnterElement, D>>,
) : Selection<EnterElement, D>(groups)

public class ExitSelection<E : Element, D>(
    groups: List<Group<E, D>>,
) : Selection<E, D>(groups)

/**
 * See analogous [d3 function](https://github.com/d3/d3-selection#selection). Note that an
 * explicit root must be specified, because there's no global document object here.
 */
public fun <E : Element> E.asSelection(): Selection<E, Nothing?> =
    Selection(listOf(Group(null, listOf(this))))
