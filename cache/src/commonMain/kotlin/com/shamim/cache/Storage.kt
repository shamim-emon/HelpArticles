package com.shamim.cache

interface Storage<K, V> {
    suspend fun put(key: K, value: V)
    suspend fun get(key: K): V?
    suspend fun remove(key: K)
    suspend fun clear()
}