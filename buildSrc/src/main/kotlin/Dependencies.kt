object kotlinx {

    fun coroutines(
        module: String = "core",
        version: String = "1.5.1"
    ) = "org.jetbrains.kotlinx:kotlinx-coroutines-$module:$version"

    fun datetime(
        version: String = "0.3.1"
    ) = "org.jetbrains.kotlinx:kotlinx-datetime:$version"
}

object androidx {

    fun startup(
        version: String = "1.0.0"
    ) = "androidx.startup:startup-runtime:$version"
}

object tuulbox {
    private const val version = "4.2.0"

    fun test(
        version: String = this.version
    ) = "com.juul.tuulbox:test:$version"
}
