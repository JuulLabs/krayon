package com.juul.krayon.kanvas

import android.content.Context
import android.graphics.Paint as AndroidPaint

public class PaintCache(
    private val context: Context,
    private val backing: MutableMap<Paint, AndroidPaint> = HashMap(),
) {
    public operator fun get(paint: Paint): AndroidPaint = synchronized(backing) {
        backing.getOrPut(paint) { paint.toAndroid(context) }
    }
}
