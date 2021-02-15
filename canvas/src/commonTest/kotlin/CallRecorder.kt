package com.juul.krayon.canvas

import kotlin.reflect.KCallable

class CallRecorder : CallRecord {

    private val _calls = mutableListOf<Call>()
    override val calls: List<Call>
        get() = _calls

    fun record(function: KCallable<*>, vararg args: Any?) {
        _calls += Call(function, args.toList())
    }
}
