package com.juul.krayon.compose

import com.juul.krayon.kanvas.MeasureText
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.TextMetrics

internal expect fun measureText(text: CharSequence, paint: Paint.Text): TextMetrics

/**
 * A [MeasureText] backed by the same native text rendering Compose uses to draw. This allows chart
 * layout code to measure text without a [ComposeKanvas].
 *
 * Fonts registered via [Krayon.addFontAssociation][com.juul.krayon.compose.addFontAssociation] are
 * honored, just as they are when drawing.
 */
public fun textMeasurement(): MeasureText = MeasureText(::measureText)
