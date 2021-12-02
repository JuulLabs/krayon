package com.juul.krayon.sample

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.math.PI
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.ExperimentalTime

/**
 * A flow that emits data for a continuously rolling sine wave. By default, updates at 60Hz and takes 10 seconds to complete a cycle.
 */
internal fun movingSineWave(
    frequency: Duration = 1000.milliseconds / 60,
    distance: Float = (2 * PI).toFloat() / 600f,
    samples: Int = 50,
): Flow<List<Point?>> = flow {
    var offset = 0f
    while (true) {
        emit(sineWave(offset, samples))
        @OptIn(ExperimentalTime::class)
        delay(frequency)
        offset += distance
    }
}

internal fun sineWave(offset: Float = 0f, samples: Int = 50): List<Point?> =
    (0 until samples).map { i ->
        val proportion = i.toFloat() / (samples - 1)
        val x = offset + (2 * PI * proportion).toFloat()
        Point(x, y = sin(x)).takeUnless { i % 5 == 0 }
    }
