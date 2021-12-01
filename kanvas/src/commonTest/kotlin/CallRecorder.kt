package com.juul.krayon.kanvas

class CallRecorder : CallRecord {

    private val _calls = mutableListOf<Call>()
    override val calls: List<Call>
        get() = _calls

    fun record(functionName: String, vararg args: Any?) {
        _calls += Call(functionName, args.toList())
    }
}
