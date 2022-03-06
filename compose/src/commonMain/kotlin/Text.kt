package com.juul.krayon.compose

import androidx.compose.runtime.Composable
import com.juul.krayon.kanvas.Paint

internal expect fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text)
