package com.juul.krayon.compose

import com.juul.krayon.core.Krayon
import com.juul.krayon.core.cache.InfiniteCache
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getSystemResourceEnvironment

internal val fontAssociations = InfiniteCache<String, NativeTypeface>()

/**
 * Registers [resource] as the font to use for [name] inside a [ComposeKanvas].
 *
 * An [environment] can be obtained by calling [getSystemResourceEnvironment]. Note that
 * [getSystemResourceEnvironment] is considered expensive, so when possible it is best to call it
 * once and reuse the result across multiple calls to this function.
 */
@Suppress("UnusedReceiverParameter")
public suspend fun Krayon.addFontAssociation(
    name: String,
    resource: FontResource,
    environment: ResourceEnvironment,
) {
    fontAssociations[name] = loadNativeTypeface(resource, environment)
}
