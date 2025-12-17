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
 * Constructs a selection with a single group containing this element. If no explicit [parent] is provided
 * for the group, then this element's parent is used.
 */
public fun <E : Element> E.asSelection(
    parent: Element? = this.parent,
): Selection<E, Nothing?> =
    Selection(listOf(Group(parent, listOf(this))))

/**
 * Constructs a selection with a single group containing these elements.  If no explicit [parent] is provided
 * for the group, then the first element's parent is used.
 */
public fun <E : Element> List<E>.asSelection(
    parent: Element? = this.firstOrNull()?.parent,
): Selection<E, Nothing?> =
    Selection(listOf(Group(parent, this)))
