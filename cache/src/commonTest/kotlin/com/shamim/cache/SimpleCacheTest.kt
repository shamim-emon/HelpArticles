package com.shamim.cache

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
        val cache = SimpleCache<String, String>(ttl) { currentTime }

        cache.put("key1", "value1")


        assertEquals("value1", cache.get("key1"))
        assertEquals(false, cache.isStale("key1"))


        currentTime += ttl.inWholeMilliseconds + 1.seconds.inWholeMilliseconds

        assertNull(cache.get("key1"))
        assertEquals(true, cache.isStale("key1"))
    }
}