package com.juul.krayon.core

/**
 * Marks an API that is experimental. Experimental Krayon APIs are intended to be usable, but are
 * more likely to design flaws or bugs. Use at your own risk!
 *
 * Experimental Krayon APIs offer no stability guarantees and may be changed without notice.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
public annotation class ExperimentalKrayonApi
