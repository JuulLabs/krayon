package com.juul.krayon.sample

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.math.PI
import kotlin.math.sin
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

/** A flow that emits data for a continuously rolling sine wave. By default, completes a full cycle in 10 seconds. */
@OptIn(ExperimentalTime::class)
internal fun movingSineWave(
    period: Duration = 10.seconds,
    samples: Int = 50,
): Flow<List<Point?>> = flow {
    var offset = 0f
    var time = TimeSource.Monotonic.markNow()
    while (currentCoroutineContext().isActive) {
        emit(sineWave(offset, samples))
        offset += ((time.elapsedNow() / period) * 2 * PI).toFloat()
        time = TimeSource.Monotonic.markNow()
        delay(1) // make sure we don't absolutely run away with CPU usage
    }
}

internal fun sineWave(offset: Float = 0f, samples: Int = 50): List<Point?> =
    (0 until samples).map { i ->
        val proportion = i.toFloat() / (samples - 1)
        val x = offset + (2 * PI * proportion).toFloat()
        Point(x, y = sin(x)).takeUnless { i % 5 == 0 }
    }
