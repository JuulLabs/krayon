package com.juul.krayon.core.cache

import com.juul.krayon.core.InternalKrayonApi
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock

@InternalKrayonApi
public class InfiniteCache<K, V> : Cache<K, V> {

    private val guard = reentrantLock()
    private val cache = HashMap<K, V>()

    override fun get(key: K): V? =
        guard.withLock { cache[key] }

    override fun set(key: K, value: V) {
        guard.withLock { cache[key] = value }
    }
}
