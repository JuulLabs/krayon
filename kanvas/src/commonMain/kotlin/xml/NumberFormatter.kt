package com.juul.krayon.kanvas.xml

/** Interface for formatting numbers in XML strings. */
public interface NumberFormatter {
    // Avoid [Number] argument to avoid boxing when possible.

    public operator fun invoke(value: Double): String
    public operator fun invoke(value: Float): String
    public operator fun invoke(value: Int): String
    public operator fun invoke(value: Long): String
}

/**
 * Implementation of [NumberFormatter] using simple [toString] calls.
 *
 * Fast, but outputs are not guaranteed to match between Kotlin runtimes.
 */
public class ToStringFormatter : NumberFormatter {
    public override fun invoke(value: Double): String = value.toString()
    public override fun invoke(value: Float): String = value.toString()
    public override fun invoke(value: Int): String = value.toString()
    public override fun invoke(value: Long): String = value.toString()
}

/**
 * Implementation of [NumberFormatter] outputting scientific notation.
 *
 * Not optimized, but guarantees consistent formatting between Kotlin runtimes.
 */
public class ScientificFormatter(private val precision: Int) : NumberFormatter {
    public override fun invoke(value: Double): String = value.scientificNotation(precision)
    public override fun invoke(value: Float): String = this(value.toDouble())
    public override fun invoke(value: Int): String = this(value.toDouble())
    public override fun invoke(value: Long): String = this(value.toDouble())
}
