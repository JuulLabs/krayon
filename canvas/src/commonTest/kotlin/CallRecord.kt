package com.juul.krayon.canvas

import kotlin.test.fail

interface CallRecord {
    val functionCalls: List<FunctionCall>
}

inline fun CallRecord.verify(
    expectation: String,
    crossinline condition: (functionCalls: List<FunctionCall>) -> Boolean,
) {
    if (!functionCalls.run(condition)) {
        fail("Unable to verify `$expectation`. Recorded calls:\n${functionCalls.joinToString(separator = "\n")}")
    }
}

inline fun CallRecord.verifyAll(
    expectation: String,
    crossinline predicate: (FunctionCall) -> Boolean,
) = verify(expectation) { it.all(predicate) }

inline fun CallRecord.verifyAny(
    expectation: String,
    crossinline predicate: (FunctionCall) -> Boolean,
) = verify(expectation) { it.any(predicate) }

fun CallRecord.verifyCallCount(expectedCount: Int) {
    verify("call count is $expectedCount") { it.size == expectedCount }
}

inline fun CallRecord.verifyFirst(
    expectation: String,
    crossinline predicate: (FunctionCall) -> Boolean,
) = verify(expectation) { it.first().run(predicate) }

inline fun CallRecord.verifyLast(
    expectation: String,
    crossinline predicate: (FunctionCall) -> Boolean,
) = verify(expectation) { it.last().run(predicate) }
