package com.juul.krayon.kanvas

import android.content.Context
import com.juul.krayon.core.cache.Cache
import com.juul.krayon.core.cache.LruCache
import android.graphics.Paint as AndroidPaint

public class PaintCache(private val context: Context) {
    private val backing: Cache<Paint, AndroidPaint> = LruCache(128)

    public operator fun get(paint: Paint): AndroidPaint {
        var androidPaint = backing[paint]
        if (androidPaint == null) {
            androidPaint = paint.toAndroid(context)
            backing[paint] = androidPaint
        }
        return androidPaint
    }
}
