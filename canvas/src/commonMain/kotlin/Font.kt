package com.juul.krayon.canvas

public const val serif: String = "serif"
public const val sansSerif: String = "sans-serif"
public const val monospace: String = "monospace"

/**
 * Fonts are provided as a list of family names, in descending priority.
 *
 * Generally, this list should end in one of the universal family names [serif], [sansSerif], or [monospace].
 */
public data class Font(
    public val names: List<String>,
) {
    public constructor(preferred: String, vararg fallback: String) : this(listOf(preferred, *fallback))

    init {
        require(names.isNotEmpty())
    }
}
