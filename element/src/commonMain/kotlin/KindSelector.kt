package com.juul.krayon.element

public fun <E: Element> ElementSelector<E>.withKind(
    kind: String
): ElementSelector<E> = KindSelector(this, kind)

internal class KindSelector<E : Element>(
    val parent: ElementSelector<E>,
    val kind: String,
) : ElementSelector<E> {

    init {
        require(parent !is KindSelector) {
            "Elements do not support multiple kinds, so nesting type-kind selectors is almost definitely a bug."
        }
    }

    override fun trySelect(element: Element): E? =
        parent.trySelect(element).takeIf { element.kind == kind }
}
