package com.juul.krayon.kanvas

import kotlin.reflect.KFunction

class CallRecorder : CallRecord {

    private val _functionCalls = mutableListOf<FunctionCall>()
    override val functionCalls: List<FunctionCall>
        get() = _functionCalls

    fun record(function: KFunction<*>, vararg args: Any?) {
        _functionCalls += FunctionCall(function, args.toList())
    }
}
