package com.juul.krayon.hierarchy

internal data class Datum(
    val name: String,
    val value: Float = 0f,
    val children: List<Datum> = emptyList(),
)

/** root -> [a -> [a1, a2], b -> [b1]] */
internal fun fixtureA(): Node<Datum, Nothing?> = hierarchy(
    Datum(
        "root",
        children = listOf(
            Datum("a", children = listOf(Datum("a1"), Datum("a2"))),
            Datum("b", children = listOf(Datum("b1"))),
        ),
    ),
) { it.children }

/** Uneven depth: root -> [a -> [a1 -> [a1x]], b] */
internal fun fixtureB(): Node<Datum, Nothing?> = hierarchy(
    Datum(
        "root",
        children = listOf(
            Datum("a", children = listOf(Datum("a1", children = listOf(Datum("a1x"))))),
            Datum("b"),
        ),
    ),
) { it.children }

/** Nested weighted leaves: root -> [a -> [a1=1, a2=2], b -> [b1=3]] */
internal fun fixtureNested(): Node<Datum, Nothing?> = hierarchy(
    Datum(
        "root",
        children = listOf(
            Datum("a", children = listOf(Datum("a1", value = 1f), Datum("a2", value = 2f))),
            Datum("b", children = listOf(Datum("b1", value = 3f))),
        ),
    ),
) { it.children }

/** root -> [a=1, b=2, c=3, d=4, e=5] weighted leaves. */
internal fun fixtureWeighted(): Node<Datum, Nothing?> = hierarchy(
    Datum(
        "root",
        children = listOf(
            Datum("a", value = 1f),
            Datum("b", value = 2f),
            Datum("c", value = 3f),
            Datum("d", value = 4f),
            Datum("e", value = 5f),
        ),
    ),
) { it.children }

internal fun <L> Node<Datum, L>.layoutByName(): Map<String, L> =
    traverseBreadthFirst().associate { it.data.name to it.layout }
