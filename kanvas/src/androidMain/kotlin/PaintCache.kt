package com.juul.krayon.kanvas

import android.content.Context
import android.graphics.Paint as AndroidPaint

public class PaintCache(
    private val context: Context,
    initial: Map<Paint, AndroidPaint> = emptyMap(),
) {
    private val backing = HashMap(initial)

    public operator fun get(paint: Paint): AndroidPaint = synchronized(backing) {
        backing.getOrPut(paint) { paint.toAndroid(context) }
    }
}
