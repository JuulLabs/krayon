package com.juul.krayon.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.HoverInteraction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Matrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.copy
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.juul.krayon.element.ClickHandler
import com.juul.krayon.element.Element
import com.juul.krayon.element.GroupElement
import com.juul.krayon.element.HoverHandler
import com.juul.krayon.element.InteractableElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TextElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.UpdateElement
import com.juul.krayon.kanvas.Paint.Text.Alignment
import com.juul.krayon.kanvas.Transform
import com.juul.krayon.kanvas.split
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

private object NotSet

@Composable
public fun <T> NativeElementView(
    dataFlow: Flow<T>,
    updateElements: UpdateElement<T>,
    modifier: Modifier = Modifier,
) {
    val dataState = remember(dataFlow) {
        dataFlow.onEach { withFrameMillis { } }
    }.collectAsState(initial = NotSet)
    if (dataState.value != NotSet) {
        @Suppress("UNCHECKED_CAST")
        NativeElementView(dataState as State<T>, updateElements, modifier)
    }
}

@Composable
public fun <T> NativeElementView(
    dataState: State<T>,
    updateElements: UpdateElement<T>,
    modifier: Modifier = Modifier,
) {
    val root = remember { RootElement() }
    var width by remember { mutableStateOf(0f) }
    var height by remember { mutableStateOf(0f) }

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
            Box(Modifier.size(newWidth.dp, newHeight.dp)) {
                root.children.forEach {
                    Element(it, transformation = remember { Matrix() })
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
    transformation: Matrix,
) {
    @Suppress("UNCHECKED_CAST")
    if (element is InteractableElement<*>) {
        InteractionHandler(element, transformation)
    }

    when (element) {
        is RootElement -> error("Unexpected root element not found at root: $element")
        is GroupElement -> element.children.forEach { Element(it, transformation) }
        is TransformElement -> {
            val transform = Matrix().apply { setFrom(transformation) }
            transform.applyTransformation(LocalDensity.current, element.transform)
            element.children.forEach { Element(it, transform) }
        }

        is NativeElement -> with(element) { Content() }
        is TextElement -> TextElement(element, transformation)

        // Fallback code path, just try our best.
        else -> Kanvas(Modifier.matchParentSize()) {
            scope.withTransform({ transform(transformation) }) {
                element.draw(this@Kanvas)
            }
        }
    }
}

@Composable
private fun BoxScope.InteractionHandler(
    element: InteractableElement<*>,
    transformation: Matrix,
) {
    // STOPSHIP: It's time to add the internal annotation
    @Suppress("UNCHECKED_CAST")
    val hoverHandler = element.attributes["hoverHandler"] as? HoverHandler<Element>

    @Suppress("UNCHECKED_CAST")
    val clickHandler = element.attributes["clickHandler"] as? ClickHandler<Element>
    if (hoverHandler == null && clickHandler == null) return // Nothing to do here

    val shape = object : Shape {
        override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
            val path = element.getInteractionPath().get(ComposePathMarker).copy()
            path.apply { transform(transformation) }
            return Outline.Generic(path)
        }
    }
    var modifier = Modifier.matchParentSize()
        .clip(shape)

    if (hoverHandler != null) {
        val interactionSource = remember { MutableInteractionSource() }
        LaunchedEffect(hoverHandler, interactionSource) {
            interactionSource.interactions.collect { interaction ->
                if (interaction is HoverInteraction.Enter) {
                    hoverHandler.onHoverChanged(element, true)
                } else if (interaction is HoverInteraction.Exit) {
                    hoverHandler.onHoverChanged(element, false)
                }
            }
        }
        modifier = modifier.hoverable(interactionSource)
    }
    if (clickHandler != null) {
        modifier = modifier.clickable(
            interactionSource = null,
            indication = null,
        ) {
            clickHandler.onClick(element)
        }
    }
    Box(modifier)
}

@Stable
private fun Matrix.applyTransformation(density: Density, transform: Transform) {
    when (transform) {
        is Transform.Skew -> error("Skew is unsupported by compose graphics layers.")

        is Transform.InOrder -> transform.transformations.forEach { applyTransformation(density, it) }

        is Transform.Translate -> with(density) {
            translate(transform.horizontal.dp.toPx(), transform.vertical.dp.toPx())
        }

        is Transform.Rotate -> if (transform.isPivoted) {
            applyTransformation(density, transform.split())
        } else {
            rotateZ(transform.degrees)
        }

        is Transform.Scale -> if (transform.isPivoted) {
            applyTransformation(density, transform.split())
        } else {
            scale(transform.horizontal, transform.vertical)
        }
    }
}

@Composable
private fun TextElement(
    element: TextElement,
    transformation: Matrix,
) {
    // Disable independent font scaling since it breaks most charts
    CompositionLocalProvider(LocalDensity provides Density(LocalDensity.current.density, fontScale = 1f)) {
        val paint = element.paint
        BasicText(
            element.text,
            Modifier
                .offset(element.x.dp, element.y.dp)
                .transform(transformation)
                .drawWithContent {
                    val translationX = when (paint.alignment) {
                        Alignment.Left -> 0f
                        Alignment.Center -> -size.width / 2f
                        Alignment.Right -> -size.width
                    }
                    val translationY = -paint.size.sp.toPx() * (1 - element.verticalAlign)
                    withTransform(transformBlock = { translate(translationX, translationY) }) {
                        this@drawWithContent.drawContent()
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

private fun Modifier.transform(transformation: Matrix) = drawWithContent {
    withTransform({ transform(transformation) }) {
        this@drawWithContent.drawContent()
    }
}
