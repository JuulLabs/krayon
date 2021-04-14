package com.juul.krayon.kanvas

import kotlin.test.fail

interface CallRecord {
    val calls: List<Call>
}

inline fun CallRecord.verify(
    expectation: String,
    crossinline condition: (calls: List<Call>) -> Boolean,
) {
    if (!calls.run(condition)) {
        fail("Unable to verify `$expectation`. Recorded calls:\n${calls.joinToString(separator = "\n")}")
    }
}

inline fun CallRecord.verifyAll(
    expectation: String,
    crossinline predicate: (Call) -> Boolean,
) = verify(expectation) { it.all(predicate) }

inline fun CallRecord.verifyAny(
    expectation: String,
    crossinline predicate: (Call) -> Boolean,
) = verify(expectation) { it.any(predicate) }

fun CallRecord.verifyCallCount(expectedCount: Int) {
    verify("call count is $expectedCount") { it.size == expectedCount }
}

inline fun CallRecord.verifyFirst(
    expectation: String,
    crossinline predicate: (Call) -> Boolean,
) = verify(expectation) { it.first().run(predicate) }

inline fun CallRecord.verifyLast(
    expectation: String,
    crossinline predicate: (Call) -> Boolean,
) = verify(expectation) { it.last().run(predicate) }

inline fun CallRecord.verifySingle(
    expectation: String,
    crossinline predicate: (Call) -> Boolean,
) = verify(expectation) { it.count(predicate) == 1 }
