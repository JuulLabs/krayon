package com.juul.krayon.element

import com.juul.krayon.color.black
import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.Paint.Text.Alignment.Left
import com.juul.krayon.kanvas.sansSerif

internal val DEFAULT_STROKE = Paint.Stroke(black, 1f)
internal val DEFAULT_FILL = Paint.Fill(black)
internal val DEFAULT_TEXT = Paint.Text(black, 12f, Left, Font(sansSerif))
