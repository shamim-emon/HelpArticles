package com.shamim.helparticles.data.network

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Singleton

@Singleton
class TestStubInterceptor(
    private val json: Json
) : Interceptor {

    private val jsonMediaType = "application/json".toMediaTypeOrNull()

    private val responses = mutableMapOf<String, StubResponse<*>>()


    fun <T> setResponse(
        path: String,
        body: T,
        serializer: KSerializer<T>,
        code: Int = 200
    ) {
        responses[path] = StubResponse(body, serializer, code)
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val path = request.url.encodedPath

        val stub = responses[path]
            ?: throw IllegalStateException("No stub response set for path: $path")


        val responseBody =
            json.encodeToString(stub.serializer as KSerializer<Any>, stub.body as Any)
        return Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(stub.code)
            .message(if (stub.code == HTTP_200) "OK" else "Error")
            .addHeader("Content-Type", "application/json")
            .body(responseBody.toResponseBody(jsonMediaType))
            .build()
    }

    private data class StubResponse<T>(
        val body: T,
        val serializer: KSerializer<T>,
        val code: Int,
    )
}