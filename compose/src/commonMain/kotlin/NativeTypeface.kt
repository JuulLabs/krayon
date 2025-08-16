package com.juul.krayon.compose

import com.juul.krayon.kanvas.Font
import com.juul.krayon.kanvas.monospace
import com.juul.krayon.kanvas.sansSerif
import com.juul.krayon.kanvas.serif
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.ResourceEnvironment

internal expect class NativeTypeface

internal expect val nativeSerif: NativeTypeface
internal expect val nativeSansSerif: NativeTypeface
internal expect val nativeMonospace: NativeTypeface

internal expect suspend fun loadNativeTypeface(
    resource: FontResource,
    environment: ResourceEnvironment,
): NativeTypeface

internal fun Font.toNativeTypeface(): NativeTypeface {
    for (name in names) {
        val association = fontAssociations[name]
        if (association != null) {
            return association
        }
        when (name) {
            serif -> return nativeSerif
            monospace -> return nativeMonospace
            sansSerif -> return nativeSansSerif
        }
    }

    return nativeSansSerif
}
