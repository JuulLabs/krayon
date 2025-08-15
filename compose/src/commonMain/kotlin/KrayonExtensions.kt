package com.juul.krayon.compose

import com.juul.krayon.core.Krayon
import kotlinx.collections.immutable.ImmutableMap
import kotlinx.collections.immutable.persistentMapOf
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.FontResource
import org.jetbrains.compose.resources.ResourceEnvironment
import org.jetbrains.compose.resources.getFontResourceBytes
import org.jetbrains.compose.resources.getSystemResourceEnvironment

private val _fontAssociations = MutableStateFlow(persistentMapOf<String, ByteArray>())
internal val fontAssociations: ImmutableMap<String, ByteArray> get() = _fontAssociations.value

@Suppress("UnusedReceiverParameter")
public suspend fun Krayon.registerFontAssociations(
    vararg fontAssociation: Pair<String, FontResource>,
    resourceEnvironment: ResourceEnvironment = getSystemResourceEnvironment(),
): Unit = registerFontAssociations(fontAssociation.toMap(), resourceEnvironment)

@Suppress("UnusedReceiverParameter")
public suspend fun Krayon.registerFontAssociations(
    associations: Map<String, FontResource>,
    resourceEnvironment: ResourceEnvironment = getSystemResourceEnvironment(),
): Unit = coroutineScope {
    for ((name, resource) in associations) {
        launch {
            val bytes = getFontResourceBytes(resourceEnvironment, resource)
            _fontAssociations.update { it.put(name, bytes) }
        }
    }
}
