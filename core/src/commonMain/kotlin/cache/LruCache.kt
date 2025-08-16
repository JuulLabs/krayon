package com.juul.krayon.core.cache

import com.juul.krayon.core.InternalKrayonApi
import kotlinx.atomicfu.locks.reentrantLock
import kotlinx.atomicfu.locks.withLock
import kotlin.math.roundToInt

@InternalKrayonApi
public class LruCache<K, V>(private val capacity: Int) : Cache<K, V> {

    init {
        check(capacity > 0) { "Capacity must be positive." }
    }

    private class Node<K, V>(val key: K, var value: V) {
        var previous: Node<K, V>? = null
        var next: Node<K, V>? = null
    }

    private val guard = reentrantLock()

    // Over-allocating capacity helps to reduce the number/cost of collisions, at the expense of memory.
    private val index = HashMap<K, Node<K, V>>((capacity * 1.25f).roundToInt(), 1f)
    private var head: Node<K, V>? = null
    private var tail: Node<K, V>? = null

    override fun get(key: K): V? {
        guard.withLock {
            val node = index[key] ?: return null
            if (node !== head) {
                remove(node)
                insert(node)
            }
            return node.value
        }
    }

    override fun set(key: K, value: V) {
        guard.withLock {
            var node = index[key]
            if (node == null) {
                if (index.size == capacity) {
                    val tail = checkNotNull(tail)
                    index.remove(tail.key)
                    remove(tail)
                }
                node = Node(key, value)
                index[key] = node
            } else {
                node.value = value
                remove(node)
            }
            insert(node)
        }
    }

    override fun getOrPut(key: K, defaultValue: () -> V): V {
        guard.withLock {
            if (key in index) {
                @Suppress("UNCHECKED_CAST")
                return get(key) as V
            } else {
                val value = defaultValue()
                set(key, value)
                return value
            }
        }
    }

    private fun insert(node: Node<K, V>) {
        node.previous = null
        node.next = head
        if (tail == null) {
            tail = node
        } else {
            head?.previous = node
        }
        head = node
    }

    private fun remove(node: Node<K, V>) {
        node.previous?.run { next = node.next }
        node.next?.run { previous = node.previous }

        if (head === node) {
            head = node.next
        }
        if (tail === node) {
            tail = node.previous
        }
    }
}
