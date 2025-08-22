package com.juul.krayon.compose

import android.graphics.Typeface
import android.graphics.fonts.Font
import android.graphics.fonts.FontFamily
import android.os.Build
import com.juul.krayon.core.Krayon
import com.juul.krayon.core.context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getFontResourceBytes
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer

internal actual typealias NativeTypeface = Typeface

internal actual val nativeSerif: NativeTypeface = Typeface.SERIF
internal actual val nativeSansSerif: NativeTypeface = Typeface.SANS_SERIF
internal actual val nativeMonospace: NativeTypeface = Typeface.MONOSPACE

internal actual suspend fun loadNativeTypeface(
    resource: FontResource,
    environment: ResourceEnvironment,
): NativeTypeface = withContext(Dispatchers.IO) {
    val bytes = getFontResourceBytes(environment, resource)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        val buffer = ByteBuffer.allocateDirect(bytes.size)
        buffer.put(bytes)
        val font = Font.Builder(buffer).build()
        val family = FontFamily.Builder(font).build()
        Typeface.CustomFallbackBuilder(family).build()
    } else {
        // Slow path for older Android versions. This is gross, but we have to copy the bytes out of assets
        // into a temporary file because `FontResource` doesn't provide an API for getting the asset-path.
        val file = File.createTempFile("fontdata", ".ttf", Krayon.context.cacheDir)
        FileOutputStream(file).use { outputStream ->
            outputStream.write(bytes)
            outputStream.flush()
        }
        Typeface.createFromFile(file).also { file.delete() }
    }
}
