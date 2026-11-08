package com.juul.krayon.core

/**
 * Marks an API that is left public for use by other Krayon modules, but is not intended for as public API.
 *
 * Internal Krayon APIs offer no stability guarantees and may be changed without notice.
 */
@MustBeDocumented
@Retention(value = AnnotationRetention.BINARY)
@RequiresOptIn(level = RequiresOptIn.Level.WARNING)
public annotation class InternalKrayonApi
