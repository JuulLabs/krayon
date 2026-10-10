@file:Suppress("DEPRECATION")
@file:OptIn(ExperimentalTestApi::class, ExperimentalKrayonApi::class)

package com.juul.krayon.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.ComposeUiTest
import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.click
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.runComposeUiTest
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.juul.krayon.core.ExperimentalKrayonApi
import com.juul.krayon.element.RectangleElement
import com.juul.krayon.element.RootElement
import com.juul.krayon.element.TransformElement
import com.juul.krayon.element.UpdateElement
import com.juul.krayon.kanvas.Transform
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class Recorder {
    var clicks: Int = 0
    var fallbackClicks: Int = 0
    var hovered: Boolean? = null
    var lastClickedTag: String? = null
}

class ComposableElementViewInteractionTests {

    private fun interactionTest(
        density: Float = 1f,
        sizeDp: Int = 100,
        buildTree: (RootElement, Recorder) -> Unit,
        assertions: ComposeUiTest.(Recorder) -> Unit,
    ) = runComposeUiTest {
        val recorder = Recorder()
        val updater = UpdateElement<Unit> { root, _, _, _ ->
            if (root.children.isEmpty()) buildTree(root, recorder)
        }
        val state = mutableStateOf(Unit)
        setContent {
            CompositionLocalProvider(LocalDensity provides Density(density)) {
                Box(Modifier.requiredSize(sizeDp.dp).testTag("view")) {
                    ComposableElementView(state, updater, Modifier.fillMaxSize())
                }
            }
        }
        waitForIdle()
        assertions(recorder)
    }

    private fun rect(
        left: Float,
        top: Float,
        right: Float,
        bottom: Float,
        block: RectangleElement.() -> Unit,
    ): RectangleElement = RectangleElement().apply {
        this.left = left
        this.top = top
        this.right = right
        this.bottom = bottom
        block()
    }

    private fun ComposeUiTest.clickAt(x: Float, y: Float) {
        onNodeWithTag("view").performMouseInput { click(Offset(x, y)) }
        waitForIdle()
    }

    private fun ComposeUiTest.moveTo(x: Float, y: Float) {
        onNodeWithTag("view").performMouseInput { moveTo(Offset(x, y)) }
        waitForIdle()
    }

    @Test
    fun click_insideRectangle_firesClickHandler() = interactionTest(
        buildTree = { root, recorder ->
            root.appendChild(rect(20f, 20f, 60f, 60f) { onClick { recorder.clicks++ } })
        },
        assertions = { recorder ->
            clickAt(40f, 40f)
            assertEquals(1, recorder.clicks)
        },
    )

    @Test
    fun click_outsideRectangle_doesNotFireButHitsFallback() = interactionTest(
        buildTree = { root, recorder ->
            root.onClickFallback = { recorder.fallbackClicks++ }
            root.appendChild(rect(20f, 20f, 60f, 60f) { onClick { recorder.clicks++ } })
        },
        assertions = { recorder ->
            clickAt(90f, 90f)
            assertEquals(0, recorder.clicks)
            assertEquals(1, recorder.fallbackClicks)
        },
    )

    @Test
    fun hover_enterAndExit_firesHoverHandler() = interactionTest(
        buildTree = { root, recorder ->
            root.appendChild(rect(20f, 20f, 60f, 60f) { onHoverChanged { _, h -> recorder.hovered = h } })
        },
        assertions = { recorder ->
            moveTo(40f, 40f)
            assertEquals(true, recorder.hovered)
            moveTo(5f, 5f)
            assertEquals(false, recorder.hovered)
        },
    )

    @Test
    fun occlusion_topMostElementReceivesClick() = interactionTest(
        buildTree = { root, recorder ->
            // Two fully-overlapping rectangles; the later (top) one must win.
            root.appendChild(rect(20f, 20f, 60f, 60f) { onClick { recorder.lastClickedTag = "bottom" } })
            root.appendChild(rect(20f, 20f, 60f, 60f) { onClick { recorder.lastClickedTag = "top" } })
        },
        assertions = { recorder ->
            clickAt(40f, 40f)
            assertEquals("top", recorder.lastClickedTag)
        },
    )

    @Test
    fun click_afterTranslate_hitRegionFollowsTransform() = interactionTest(
        buildTree = { root, recorder ->
            val transform = TransformElement().apply { transform = Transform.Translate(40f, 40f) }
            root.appendChild(transform)
            transform.appendChild(rect(0f, 0f, 20f, 20f) { onClick { recorder.clicks++ } })
        },
        assertions = { recorder ->
            clickAt(10f, 10f) // pre-translate position, should miss
            assertEquals(0, recorder.clicks)
            clickAt(50f, 50f) // translated center (40+10), should hit
            assertEquals(1, recorder.clicks)
        },
    )

    @Test
    fun click_afterScale_hitRegionFollowsTransform() = interactionTest(
        buildTree = { root, recorder ->
            val transform = TransformElement().apply { transform = Transform.Scale(2f, 2f) }
            root.appendChild(transform)
            transform.appendChild(rect(10f, 10f, 20f, 20f) { onClick { recorder.clicks++ } })
        },
        assertions = { recorder ->
            clickAt(15f, 15f) // pre-scale position, should miss (scaled rect is [20..40])
            assertEquals(0, recorder.clicks)
            clickAt(30f, 30f) // scaled center, should hit
            assertEquals(1, recorder.clicks)
        },
    )

    @Test
    fun click_afterRotatePivoted_hitRegionFollowsTransform() = interactionTest(
        buildTree = { root, recorder ->
            // Rotate 90° about the rectangle's own center; the region should stay put (square).
            val transform = TransformElement().apply {
                transform = Transform.Rotate(90f, pivotX = 40f, pivotY = 40f)
            }
            root.appendChild(transform)
            transform.appendChild(rect(20f, 30f, 60f, 50f) { onClick { recorder.clicks++ } })
        },
        assertions = { recorder ->
            // After a 90° rotation about (40,40), the wide rect [20..60]x[30..50] maps to [30..50]x[20..60].
            clickAt(40f, 55f) // inside rotated region, outside original
            assertEquals(1, recorder.clicks)
            clickAt(55f, 40f) // inside original region, outside rotated
            assertEquals(1, recorder.clicks) // still 1 -> this point should NOT add a click
        },
    )

    @Test
    fun click_atNonUnitDensity_mapsCoordinatesCorrectly() = interactionTest(
        density = 2f,
        sizeDp = 50, // 100px at density 2; Krayon space is 0..50
        buildTree = { root, recorder ->
            root.appendChild(rect(10f, 10f, 40f, 40f) { onClick { recorder.clicks++ } })
        },
        assertions = { recorder ->
            // Krayon rect [10..40] -> screen px [20..80] at density 2.
            clickAt(15f, 15f) // px inside the buggy (unscaled) region but outside the correct one -> miss
            assertEquals(0, recorder.clicks)
            clickAt(60f, 60f) // px = Krayon (30,30), inside correct region -> hit
            assertEquals(1, recorder.clicks)
        },
    )

    @Test
    fun hover_afterTranslate_atNonUnitDensity() = interactionTest(
        density = 2f,
        sizeDp = 50,
        buildTree = { root, recorder ->
            val transform = TransformElement().apply { transform = Transform.Translate(10f, 10f) }
            root.appendChild(transform)
            transform.appendChild(rect(0f, 0f, 20f, 20f) { onHoverChanged { _, h -> recorder.hovered = h } })
        },
        assertions = { recorder ->
            // Krayon rect after translate: [10..30]; at density 2 -> px [20..60].
            assertNull(recorder.hovered)
            moveTo(40f, 40f) // px = Krayon (20,20), inside
            assertEquals(true, recorder.hovered)
            moveTo(10f, 10f) // px = Krayon (5,5), outside
            assertTrue(recorder.hovered == false)
        },
    )
}
