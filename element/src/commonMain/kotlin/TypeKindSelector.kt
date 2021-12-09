package com.juul.krayon.element

public fun <E: Element> TypeSelector<E>.withKind(kind: String): TypeSelector<E> =
    TypeKindSelector(this, kind)

internal class TypeKindSelector<E : Element>(
    val parent: TypeSelector<E>,
    val kind: String,
) : TypeSelector<E> {

    init {
        require(parent !is TypeKindSelector) {
            "Elements do not support multiple kinds, so nesting type-kind selectors is almost definitely a bug."
        }
    }

    override fun trySelect(element: Element): E? =
        parent.trySelect(element).takeIf { element.kind == kind }
}
