package com.juul.krayon.kanvas

/**
 * Capability for measuring text without drawing it, enabling layout that is aware of how large text
 * will be.
 *
 * This mirrors [IsPointInPath]: rendering backends that are able to measure text implement this
 * interface (for example, the platform [Kanvas] implementations), and standalone implementations
 * exist for use where a [Kanvas] is not available, such as chart layout code. Backends that have no
 * font engine, like [com.juul.krayon.kanvas.svg.SvgKanvas], do not support measurement.
 */
public fun interface MeasureText {

    /**
     * Measures [text] as it would be drawn with [paint]. See [TextMetrics] for the meaning and units
     * of the returned values.
     */
    public fun measureText(text: CharSequence, paint: Paint.Text): TextMetrics
}
