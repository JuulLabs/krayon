package com.juul.krayon.core.cache

import com.juul.krayon.core.InternalKrayonApi

@InternalKrayonApi
public interface Cache<K, V> {

    public operator fun get(key: K): V?

    public operator fun set(key: K, value: V)

    public fun getOrPut(key: K, defaultValue: () -> V): V
}
