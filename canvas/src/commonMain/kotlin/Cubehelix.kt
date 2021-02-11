package com.juul.krayon.canvas

import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.pow
import kotlin.math.sin

/** Implementation of [cubehelix](http://www.mrao.cam.ac.uk/~dag/CUBEHELIX/), a black-and-white-printer friendly color scheme. */
public data class Cubehelix(
    public val start: Float = 0.5f,
    public val rotations: Float = -1.5f,
    public val gamma: Float = 1f,
    public val minSaturation: Float = 1.2f,
    public val maxSaturation: Float = 1.2f,
    public val minLightness: Float = 0f,
    public val maxLightness: Float = 1f,
    public val resolution: Int = 32,
) : LinearPalette {

    init {
        require(resolution > 1)
    }

    private val colors by lazy {
        (0 until resolution).map { step ->
            val percent = step.toFloat() / (resolution - 1).toFloat()
            val rawLightness = minLightness + (maxLightness - minLightness) * percent
            val angle = 2 * PI * (start / 3.0 + rotations * percent)
            val lightness = rawLightness.pow(gamma)
            val saturation = minSaturation + (maxSaturation - minSaturation) * percent
            val amplitude = saturation * lightness * (1 - lightness) / 2
            val red = lightness + amplitude * (-0.14861f * cos(angle).toFloat() + 1.78277f * sin(angle).toFloat()).coerceIn(0f, 1f)
            val green = lightness + amplitude * (-0.29227f * cos(angle).toFloat() + 0.90649f * sin(angle).toFloat()).coerceIn(0f, 1f)
            // No sin because the scaling factor is 0 in the source matrix.
            val blue = lightness + amplitude * (1.97294f * cos(angle).toFloat()).coerceIn(0f, 1f)
            Color(red, green, blue)
        }
    }

    override fun getColor(percent: Float): Color {
        require(percent in 0f..1f)
        return when (percent) {
            0f -> colors[0]
            1f -> colors[resolution - 1]
            else -> {
                val fractionalIndex = percent * (resolution - 1)
                val startIndex = fractionalIndex.toInt()
                val endIndex = startIndex + 1
                colors[startIndex].lerp(colors[endIndex], fractionalIndex - startIndex)
            }
        }
    }
}
