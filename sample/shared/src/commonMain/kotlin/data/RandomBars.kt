package com.juul.krayon.sample.data

import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.isActive
import kotlin.random.Random
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * Emits a fresh set of [count] random bar values (each in `0f..1f`) every [period], for demonstrating
 * animated transitions between data states.
 */
fun randomBars(
    count: Int = 12,
    period: Duration = 2.seconds,
): Flow<List<Float>> = flow {
    while (currentCoroutineContext().isActive) {
        emit(List(count) { Random.nextFloat() })
        delay(period)
    }
}
