package com.shamim.cache

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class SimpleCacheTest {

    private val ttl = 1.days

    @Test
    fun `cache stores value and expires after TTL`() = runTest {
        var currentTime = 0L
        val cache = SimpleCache<String, String>(ttl = ttl, storage = FakeStorage()) { currentTime }

        cache.put("key1", "value1")


        assertEquals("value1", cache.get("key1"))
        assertEquals(false, cache.isStale("key1"))


        currentTime += ttl.inWholeMilliseconds + 1.seconds.inWholeMilliseconds

        assertNull(cache.get("key1"))
        assertEquals(true, cache.isStale("key1"))
    }
}

private class FakeStorage<K, V> : Storage<K, V> {
    private val map = mutableMapOf<K, V>()
    private val mutex = Mutex()

    override suspend fun put(key: K, value: V) = mutex.withLock {
        map[key] = value
    }

    override suspend fun get(key: K): V? = mutex.withLock {
        map[key]
    }

    override suspend fun remove(key: K):Unit  = mutex.withLock {
        map.remove(key)
    }

    override suspend fun clear() = mutex.withLock {
        map.clear()
    }
}
