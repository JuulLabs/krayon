package com.juul.krayon.kanvas

/**
 * The measured size of a string for a given [Paint.Text].
 *
 * All values use the same units as the [Paint.Text.size] passed to measurement, which are the same
 * units used when drawing. This is a primitive, single-line measurement: no line wrapping is
 * performed and newline characters are not treated specially.
 */
public data class TextMetrics(
    /** Horizontal advance width of the text. */
    public val width: Float,
    /** Distance from the text baseline to the top of the text, measured upward (non-negative). */
    public val ascent: Float,
    /** Distance from the text baseline to the bottom of the text, measured downward (non-negative). */
    public val descent: Float,
) {
    /** Total vertical extent of the text, equal to [ascent] + [descent]. */
    public val height: Float get() = ascent + descent
}
