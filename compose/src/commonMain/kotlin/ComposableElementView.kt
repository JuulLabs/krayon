package com.juul.krayon.compose

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Enter
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Exit
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Move
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Press
import androidx.compose.ui.input.pointer.PointerEventType.Companion.Release
import androidx.compose.ui.input.pointer.PointerType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juul.krayon.core.ExperimentalKrayonApi
import com.juul.krayon.element.ClipElement
import com.juul.krayon.element.Element
import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.UpdateElement
import com.juul.krayon.kanvas.Paint.Text.Alignment
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlin.math.roundToInt

private object NotSet

/**
 * Experiment API subject to change.
 *
 * Renders an element tree using [Composable] functions where appropriate. This is almost certainly slower than using
 * the single-canvas [ElementView], but has other advantages, such as allowing [ComposableElement]s to emit real
 * composables into the layout. Interaction (hover/click) is dispatched through the same [RootElement] hit-testing used
 * by [ElementView], so it behaves identically, including under transforms.
 */
@Composable
@ExperimentalKrayonApi
public fun <T> ComposableElementView(
    dataFlow: Flow<T>,
    updateElements: UpdateElement<T>,
    modifier: Modifier = Modifier,
) {
    val dataState = remember(dataFlow) {
        dataFlow.onEach { withFrameMillis { } }
    }.collectAsState(initial = NotSet)
    if (dataState.value != NotSet) {
        @Suppress("UNCHECKED_CAST")
        ComposableElementView(dataState as State<T>, updateElements, modifier)
    }
}

@Composable
@ExperimentalKrayonApi
public fun <T> ComposableElementView(
    dataState: State<T>,
    updateElements: UpdateElement<T>,
    modifier: Modifier = Modifier,
) {
    val root = remember { RootElement() }
    val transformation = remember { ImmutableMatrix() }
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

    // Dirty hack for correctly handling hover after taps, mirroring [ElementView]. Some platforms don't propagate the
    // release event after a tap, so we flag the next touch event as an exit instead.
    var treatNextTouchAsExit by remember { mutableStateOf(false) }

    fun update(data: T) {
        if (width != 0f && height != 0f) {
            updateElements.update(root, width, height, data)
        }
    }

    LaunchedEffect(dataState, updateElements) {
        snapshotFlow { dataState.value }.collect { data ->
            withFrameMillis { /* Nothing */ }
            update(data)
        }
    }

    SubcomposeLayout(modifier) { constraints ->
        val newWidth = constraints.maxWidth.toDp().value
        val newHeight = constraints.maxHeight.toDp().value
        if (width != newWidth || height != newHeight) {
            width = newWidth
            height = newHeight
            update(dataState.value)
        }

        val measurable = subcompose("content") {
            Box(
                Modifier
                    .size(newWidth.dp, newHeight.dp)
                    .pointerInput(root) { // hover events must be processed manually
                        while (currentCoroutineContext().isActive) {
                            val event = awaitPointerEventScope { awaitPointerEvent(PointerEventPass.Main) }
                            val change = event.changes.last()
                            if (treatNextTouchAsExit) {
                                treatNextTouchAsExit = false
                                if (change.type == PointerType.Touch) {
                                    root.onHoverEnded()
                                }
                            } else {
                                when (event.type) {
                                    Press, Enter, Move -> {
                                        val (x, y) = change.position
                                        root.onHover(isPointInPath(), x.toDp().value, y.toDp().value)
                                    }
                                    Release -> {
                                        // Sometimes this event doesn't fire correctly and we get a move
                                        // instead. Still, handle it properly in case they ever fix it.
                                        if (change.type == PointerType.Touch) {
                                            root.onHoverEnded()
                                        } else {
                                            val (x, y) = change.position
                                            root.onHover(isPointInPath(), x.toDp().value, y.toDp().value)
                                        }
                                    }
                                    Exit -> root.onHoverEnded()
                                }
                            }
                        }
                    }
                    .pointerInput(root) { // tap events have such nice syntax sugar
                        detectTapGestures(
                            onTap = { offset ->
                                val x = offset.x.toDp().value
                                val y = offset.y.toDp().value
                                root.onClick(isPointInPath(), x, y)
                                treatNextTouchAsExit = true
                            },
                        )
                    },
            ) {
                root.children.forEach {
                    Element(it, transformation)
                }
            }
        }.single()
        val placeable = measurable.measure(constraints)

        layout(constraints.maxWidth, constraints.maxHeight) {
            placeable.place(0, 0)
        }
    }
}

@Composable
private fun BoxScope.Element(
    element: Element,
    transformation: ImmutableMatrix,
) {
    when (element) {
        is RootElement -> error("Unexpected root element not found at root: $element")

        is GroupElement -> element.children.forEach { Element(it, transformation) }

        is ClipElement -> {
            val clip = element.clip?.path?.get(ComposePathMarker)?.copy()?.apply { transform(transformation) }
            if (clip == null) {
                element.children.forEach { Element(it, transformation) }
            } else {
                Box(
                    Modifier.matchParentSize().drawWithContent {
                        clipPath(clip) {
                            this@drawWithContent.drawContent()
                        }
                    },
                ) {
                    element.children.forEach { Element(it, transformation) }
                }
            }
        }

        is TransformElement -> {
            val childTransformation = transformation.withTransform(LocalDensity.current, element.transform)
            element.children.forEach { Element(it, childTransformation) }
        }

        is ComposableElement -> with(element) { Content(transformation) }

        is TextElement -> TextElement(element, transformation)

        // Fallback code path, render it on a Kanvas instead of with @Composables
        else -> Kanvas(Modifier.matchParentSize()) {
            scope.withTransform({ transform(transformation) }) {
                element.draw(this@Kanvas)
            }
        }
    }
}

@Composable
private fun TextElement(
    element: TextElement,
    transformation: ImmutableMatrix,
) {
    // Disable independent font scaling since it breaks most charts
    val density = Density(LocalDensity.current.density, fontScale = 1f)
    CompositionLocalProvider(
        LocalDensity provides density,
    ) {
        val paint = element.paint
        BasicText(
            element.text,
            Modifier
                .absoluteOffset(x = element.x.dp, y = element.y.dp)
                .absoluteOffset(x = 0.dp, y = with(density) { -paint.size.sp.toDp() } * (1 - element.verticalAlign))
                .transform(transformation)
                .layout { measurable, constraints ->
                    val placeable = measurable.measure(constraints)
                    layout(placeable.width, placeable.height) {
                        val offsetX = when (paint.alignment) {
                            Alignment.Left -> 0f
                            Alignment.Center -> -placeable.width / 2f
                            Alignment.Right -> -placeable.width.toFloat()
                        }.roundToInt()
                        placeable.placeRelative(offsetX, 0)
                    }
                },
            style = TextStyle(
                paint.color.toCompose(),
                fontSize = paint.size.sp,
                textAlign = when (paint.alignment) {
                    Alignment.Left -> TextAlign.Left
                    Alignment.Center -> TextAlign.Center
                    Alignment.Right -> TextAlign.Right
                },
            ),
        )
    }
}
