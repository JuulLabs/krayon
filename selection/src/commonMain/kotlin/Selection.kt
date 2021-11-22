package com.juul.krayon.selection

import com.juul.krayon.element.RootElement

public open class Selection<T>(
    public val groups: List<Group<T>>
)

public class UpdateSelection<T>(
    groups: List<Group<T>>,
    public val enter: EnterSelection<T>,
    public val exit: ExitSelection<T>
) : Selection<T>(groups)

public class EnterSelection<T>(
    groups: List<Group<T>>
) : Selection<T>(groups)

public class ExitSelection<T>(
    groups: List<Group<T>>
) : Selection<T>(groups)

public fun RootElement.selection(): Selection<Nothing?> =
    Selection(listOf(Group(null, listOf(this))))
