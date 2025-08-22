package com.juul.krayon.compose

import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getFontResourceBytes
import org.jetbrains.skia.Data
import org.jetbrains.skia.FontMgr
import org.jetbrains.skia.FontStyle
import org.jetbrains.skia.Typeface

internal actual typealias NativeTypeface = Typeface

internal actual val nativeSerif: NativeTypeface by lazy {
    checkNotNull(FontMgr.default.legacyMakeTypeface("serif", FontStyle.NORMAL))
}

internal actual val nativeSansSerif: NativeTypeface by lazy {
    checkNotNull(FontMgr.default.legacyMakeTypeface("sans-serif", FontStyle.NORMAL))
}

internal actual val nativeMonospace: NativeTypeface by lazy {
    checkNotNull(FontMgr.default.legacyMakeTypeface("monospace", FontStyle.NORMAL))
}

internal actual suspend fun loadNativeTypeface(
    resource: FontResource,
    environment: ResourceEnvironment,
): NativeTypeface {
    val bytes = getFontResourceBytes(environment, resource)
    val data = Data.makeFromBytes(bytes)
    return checkNotNull(FontMgr.default.makeFromData(data))
}
