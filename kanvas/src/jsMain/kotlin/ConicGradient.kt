package com.juul.krayon.kanvas

import org.khronos.webgl.Uint8ClampedArray
import org.khronos.webgl.get
import org.w3c.dom.CanvasFillStrokeStyles
import org.w3c.dom.CanvasGradient
import org.w3c.dom.CanvasImageData
import org.w3c.dom.CanvasRect
import org.w3c.dom.ImageBitmap
import org.w3c.dom.RenderingContext
import kotlin.math.PI
import kotlin.math.nextUp

/** Workaround for new JS functionality that hasn't made its way to Kotlin stdlib yet. */
internal external interface ConicCanvasFillStrokeStyles : CanvasFillStrokeStyles {
    fun createConicGradient(startAngle: Double, x: Double, y: Double): CanvasGradient
}

/** Workaround for new JS functionality that hasn't made its way to Kotlin stdlib yet. */
private external class OffscreenCanvas(width: Double, height: Double) {
    fun getContext(contextId: String, vararg arguments: Any?): RenderingContext?
    fun transferToImageBitmap(): ImageBitmap
}

/**
 * Returns the browser-native start angle for conic gradients that will produce behavior matching
 * Krayon's expectations. On some browsers this is 0, and on other browsers this is PI/2.
 */
internal val conicStartAngleOffset: Double by lazy {
    // Use off-screen rendering to figure out runtime behavior.
    val canvas = try {
        OffscreenCanvas(2.0, 2.0)
    } catch (e: Throwable) {
        // Safari is the only (major?) browser that doesn't support this, which ends up being okay:
        // we've successfully identified rendering behavior.
        val isReferenceError = js("e instanceof ReferenceError") as Boolean
        if (isReferenceError) {
            return@lazy PI / 2
        } else {
            throw e
        }
    }

    // Render a conic gradient that's half black and half white, starting at browser-native 0.
    val context = canvas.getContext("2d") as ConicCanvasFillStrokeStyles
    context.fillStyle = context.createConicGradient(startAngle = 0.0, x = 1.0, y = 1.0).apply {
        addColorStop(0.0, "black")
        addColorStop(0.5, "black")
        addColorStop(0.5.nextUp(), "white")
        addColorStop(1.0, "white")
    }
    (context as CanvasRect).fillRect(0.0, 0.0, 2.0, 2.0)

    // Average value of RGB channels in top-right pixel.
    val rgbAverage = (context as CanvasImageData)
        .getImageData(1.0, 0.0, 1.0, 1.0)
        .data
        .asSequence()
        .take(3)
        .average()
    if (rgbAverage > 0.5) { // Browser uses 0-right start angle, so no offset is required
        0.0
    } else { // Browser uses 0-up start angle, so offset by 90 degrees
        PI / 2
    }
}

/** Zero-copy conversion from JS-native type to play nicer with the Kotlin standard library. */
private fun Uint8ClampedArray.asSequence(): Sequence<Byte> = sequence {
    for (index in 0 until length) {
        yield(get(index))
    }
}
