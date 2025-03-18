package com.juul.krayon.element.view

import android.graphics.Bitmap
import android.graphics.Color
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import androidx.core.graphics.createBitmap

/** Cache of no-longer-used [Bitmap] that might be useful in the future, to avoid allocating on every draw. */
internal class BitmapPool(
    private val config: Bitmap.Config = Bitmap.Config.ARGB_8888,
) {
    /** Pixel count of the most recently requested bitmap. Used to clear old entries from the pool when an [ElementView] grows. */
    private var size: Long = 0
    private val mutex = Mutex()
    private val pool = ArrayDeque<Bitmap>()

    suspend fun acquire(width: Int, height: Int): Bitmap = mutex.withLock {
        val newSize = width.toLong() * height
        if (newSize > size) {
            pool.forEach { it.recycle() }
            pool.clear()
        }
        size = newSize
        pool.removeFirstOrNull()
            ?.also { bmp -> if (bmp.width != width || bmp.height != height) bmp.reconfigure(width, height, config) }
            ?: createBitmap(width, height, config)
    }

    suspend fun release(bitmap: Bitmap) = mutex.withLock {
        if (bitmap.width.toLong() * bitmap.height >= size) {
            bitmap.eraseColor(Color.TRANSPARENT)
            pool.addLast(bitmap)
        } else {
            bitmap.recycle()
        }
    }
}
