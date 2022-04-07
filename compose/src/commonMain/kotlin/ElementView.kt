package com.juul.krayon.compose

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import com.juul.krayon.element.RootElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock

@Composable
public fun <T> ElementView(
    deriveData: () -> T,
    updateElements: (root: RootElement, width: Float, height: Float, data: T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val state = remember { derivedStateOf(deriveData) }
    ElementView(state, updateElements, modifier)
}

@Composable
public fun <T> ElementView(
    dataState: State<T>,
    updateElements: (root: RootElement, width: Float, height: Float, data: T) -> Unit,
    modifier: Modifier = Modifier,
) {
    val flow = remember(dataState) { snapshotFlow { dataState.value } }
    ElementView(flow, updateElements, modifier)
}

@Composable
public fun <T> ElementView(
    dataSource: Flow<T>,
    updateElements: (root: RootElement, width: Float, height: Float, data: T) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        // Was having problems getting the sizes to update when resizing the window. This little dance worked
        // when more obvious solutions didn't
        fun getWidth() = minWidth.value
        fun getHeight() = minHeight.value
        val width = remember { MutableStateFlow(getWidth()) }
        val height = remember { MutableStateFlow(getHeight()) }
        SideEffect {
            width.value = getWidth()
            height.value = getHeight()
        }
        val root = remember { RootElement() }
        // TODO: Figure out a better method of forcing the canvas to recompose every update.
        val frameTime = remember { mutableStateOf(Clock.System.now()) }
        LaunchedEffect(dataSource, updateElements) {
            combine(dataSource, width, height) { data, width, height ->
                Triple(data, width, height)
            }.collect { (data, width, height) ->
                withFrameMillis { // We don't actually need the frame time here, but this causes sync with the device framerate
                    if (width > 0 && height > 0) { // It's possible to have a negative size in compose. No-op in that case.
                        updateElements(root, width, height, data)
                        frameTime.value = Clock.System.now()
                    }
                }
            }
        }
        Kanvas(Modifier.matchParentSize()) {
            frameTime.value // Be reading a state that changed
            root.draw(this)
        }
    }
}
