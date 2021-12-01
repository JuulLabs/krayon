package com.juul.krayon.kanvas

import kotlin.test.fail

interface CallRecord {
    val calls: List<Call>
}

fun CallRecord.verify(
    expectation: String,
    condition: (calls: List<Call>) -> Boolean,
) {
    if (!calls.run(condition)) {
        fail("Unable to verify `$expectation`. Recorded calls:\n${calls.joinToString(separator = "\n")}")
    }
}

fun CallRecord.verifyAll(
    expectation: String,
    predicate: (Call) -> Boolean,
) = verify(expectation) { it.all(predicate) }

fun CallRecord.verifyAny(
    expectation: String,
    predicate: (Call) -> Boolean,
) = verify(expectation) { it.any(predicate) }

fun CallRecord.verifyCallCount(expectedCount: Int) {
    verify("call count is $expectedCount") { it.size == expectedCount }
}

fun CallRecord.verifyFirst(
    expectation: String,
    predicate: (Call) -> Boolean,
) = verify(expectation) { it.first().run(predicate) }

fun CallRecord.verifyLast(
    expectation: String,
    predicate: (Call) -> Boolean,
) = verify(expectation) { it.last().run(predicate) }

fun CallRecord.verifySingle(
    expectation: String,
    predicate: (Call) -> Boolean,
) = verify(expectation) { it.count(predicate) == 1 }
