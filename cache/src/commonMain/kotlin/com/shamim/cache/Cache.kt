package com.shamim.cache

import kotlin.time.Duration
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.Serializable
import kotlin.time.Duration.Companion.hours

interface Cache<K, V> {
    suspend fun put(key: K, value: V)
    suspend fun get(key: K): V?
    suspend fun isStale(key: K): Boolean
    suspend fun remove(key: K)
    suspend fun clear()
}



class SimpleCache<K, V>(
    private val ttl: Duration = 24.hours,
    private val storage:  Storage<K, CacheItem<V>>,
    private val clock: () -> Long) : Cache<K, V> {

    private val mutex = Mutex()

    override suspend fun put(key: K, value: V) = mutex.withLock {
        storage.put(key, CacheItem(value, clock()))
    }

    override suspend fun get(key: K): V? = mutex.withLock {
        val now = clock()
        val item = storage.get(key)?: return null

        return if (item.isExpired(now)) {
            storage.remove(key)
            null
        } else {
            item.value
        }
    }

    override suspend fun isStale(key: K): Boolean = mutex.withLock {
        val item = storage.get(key) ?: return true
        item.isExpired(clock())
    }

    override suspend fun remove(key: K): Unit = mutex.withLock {
        storage.remove(key)
    }

    override suspend fun clear() = mutex.withLock {
        storage.clear()
    }

    private fun CacheItem<V>.isExpired(now: Long): Boolean =
        now - timestamp > ttl.inWholeMilliseconds

}

@Serializable
data class CacheItem<V>(val value: V, val timestamp: Long)