package com.juul.krayon.kanvas.compose

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import com.juul.krayon.element.RootElement
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.datetime.Clock

@Composable
fun <T> ElementView(
    dataSource: Flow<T>,
    updateElements: (root: RootElement, width: Float, height: Float, data: T) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier) {
        fun getWidth() = minWidth.value
        fun getHeight() = minHeight.value
        val root = remember { RootElement() }
        val width = remember { MutableStateFlow(getWidth()) }
        val height = remember { MutableStateFlow(getHeight()) }
        SideEffect {
            width.value = getWidth()
            height.value = getHeight()
        }
        // TODO: Figure out a better method of forcing the canvas to recompose every update.
        val frameTime = remember { mutableStateOf(Clock.System.now()) }
        LaunchedEffect(dataSource, updateElements) {
            combine(dataSource, width, height) { data, width, height ->
                Triple(data, width, height)
            }.collect { (data, width, height) ->
                withFrameMillis {
                    if (width > 0 && height > 0) {
                        updateElements(root, width, height, data)
                        frameTime.value = Clock.System.now()
                    }
                }
            }
        }
        Kanvas(Modifier.matchParentSize()) {
            frameTime.value
            root.draw(this)
        }
    }
}
