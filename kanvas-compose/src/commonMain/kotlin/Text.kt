package com.juul.krayon.kanvas.compose

import androidx.compose.runtime.Composable
import com.juul.krayon.kanvas.Paint

// TODO: Figure out why this doesn't compile when marked internal instead of public
public expect class ResourceContext

@Composable
internal expect fun rememberResourceContext(): ResourceContext

internal expect fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text)
