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

    fun appCompat(
        submodule: String? = null,
        version: String = "1.4.0"
    ) = when (submodule) {
        null -> "androidx.appcompat:appcompat:$version"
        else -> "androidx.appcompat:appcompat-$submodule:$version"
    }

    fun lifecycle(
        submodule: String,
        version: String = "2.4.0"
    ) = "androidx.lifecycle:lifecycle-$submodule:$version"

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
