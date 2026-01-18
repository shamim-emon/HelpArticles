package com.shamim.cache

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import java.io.File

class FileStorage<K, V>(
    private val context: Context,
    private val serializer: KSerializer<V>,
    private val keyToFileName: (K) -> String = { sanitizeFileName(it.toString()) },
    private val json: Json = Json { ignoreUnknownKeys = true }
) : Storage<K, V> {

    private val storageDir: File by lazy {
        File(context.filesDir, "file_storage").apply { mkdirs() }
    }

    override suspend fun put(key: K, value: V) = withContext(Dispatchers.IO) {
        try {
            val file = File(storageDir, keyToFileName(key))
            val jsonString = json.encodeToString(serializer, value)
            file.writeText(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun get(key: K): V? = withContext(Dispatchers.IO) {
        try {
            val file = File(storageDir, keyToFileName(key))
            if (!file.exists()) return@withContext null
            val jsonString = file.readText()
            json.decodeFromString(serializer, jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun remove(key: K) = withContext(Dispatchers.IO) {
        try {
            val file = File(storageDir, keyToFileName(key))
            if (file.exists() && !file.delete()) {
                println("Failed to delete file: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun clear(): Unit = withContext(Dispatchers.IO) {
        try {
            storageDir.listFiles()?.forEach { file ->
                if (!file.delete()) println("Failed to delete file: ${file.absolutePath}")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        private fun sanitizeFileName(name: String): String =
            name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }
}
