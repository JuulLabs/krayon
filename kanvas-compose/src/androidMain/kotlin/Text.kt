package com.juul.krayon.kanvas.compose

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import com.juul.krayon.kanvas.Paint
import com.juul.krayon.kanvas.toAndroid

public actual typealias ResourceContext = Context

@Composable
internal actual fun rememberResourceContext(): ResourceContext = LocalContext.current

internal actual fun drawText(kanvas: ComposeKanvas, text: CharSequence, x: Float, y: Float, paint: Paint.Text) {
    val canvas = kanvas.scope.drawContext.canvas.nativeCanvas
    canvas.drawText(text.toString(), x, y, paint.toAndroid(kanvas.resourceContext))
}
