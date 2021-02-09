package com.juul.krayon.canvas

import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface CallRecord {
    val functionCalls: List<FunctionCall>
}

inline fun CallRecord.verify(
    expectation: String,
    crossinline condition: (functionCalls: List<FunctionCall>) -> Boolean
) {
    assertTrue("Unable to verify `$expectation`.") { functionCalls.run(condition) }
}

inline fun CallRecord.verifyAll(
    expectation: String,
    crossinline predicate: (FunctionCall) -> Boolean
) = verify(expectation) { it.all(predicate) }

inline fun CallRecord.verifyAny(
    expectation: String,
    crossinline predicate: (FunctionCall) -> Boolean
) = verify(expectation) { it.any(predicate) }

fun CallRecord.verifyCallCount(expectedCount: Int) {
    assertEquals(expectedCount, functionCalls.size, "Unable to verify `recorded call count is $expectedCount`.")
}

inline fun CallRecord.verifyFirst(
    expectation: String,
    crossinline predicate: (FunctionCall) -> Boolean
) = verify(expectation) { it.first().run(predicate) }

inline fun CallRecord.verifyLast(
    expectation: String,
    crossinline predicate: (FunctionCall) -> Boolean
) = verify(expectation) { it.last().run(predicate) }
