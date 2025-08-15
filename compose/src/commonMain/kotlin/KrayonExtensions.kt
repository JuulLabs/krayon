package com.juul.krayon.compose

import com.juul.krayon.core.Krayon
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.update
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getFontResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment

private val _fontAssociations = atomic(persistentMapOf<String, ByteArray>())
internal val fontAssociations: ImmutableMap<String, ByteArray> get() = _fontAssociations.value

/**
 * Registers [resource] as the font to use for [name] inside Compose canvases.
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
    val bytes = getFontResourceBytes(environment, resource)
    _fontAssociations.update { it.put(name, bytes) }
}
