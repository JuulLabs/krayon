package com.juul.krayon.kanvas.compose

import com.juul.krayon.kanvas.Paint

internal expect fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text)
