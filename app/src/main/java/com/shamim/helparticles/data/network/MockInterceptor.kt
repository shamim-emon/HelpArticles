package com.shamim.helparticles.data.network

import com.shamim.helparticles.NetworkConnectivityChecker
import com.shamim.helparticles.data.model.Article
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class MockInterceptor(
    private val networkConnectivityChecker: NetworkConnectivityChecker,
    private val json: Json,
    private val timeoutProbability: Int,
    private val serverErrorProbability: Int,
    private val malformedResponseProbability: Int,
    private val emptyArticleListProbability: Int,
    private val randomProvider: () -> Int
) : Interceptor {

    private val jsonMediaType = "application/json".toMediaTypeOrNull()
    private val serverError = ErrorResponse(
        errorCode = HTTP_500,
        isServerError = true,
        errorTitle = "Internal Server error",
        errorMessage = "Something went wrong. Please try again later."
    )

    private val contentNotFoundError = ErrorResponse(
        errorCode = HTTP_404,
        isServerError = false,
        errorTitle = "Item Not Found",
        errorMessage = "This content does not exists anymore."
    )

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!networkConnectivityChecker.isOnline())
            throw UnknownHostException("No internet connection")

        val request = chain.request()
        val path = request.url.encodedPath

        val random = randomProvider()

        if (random < timeoutProbability)
            throw SocketTimeoutException("Timeout")
        if (random < timeoutProbability + serverErrorProbability) {
            val str = json.encodeToString(serverError)
            return buildResponse(request, serverError.errorCode, str)
        }
        if (random < timeoutProbability + serverErrorProbability + malformedResponseProbability) {
            val malformedJson = "{ this is not valid JSON! }"
            return buildResponse(request, HTTP_200, malformedJson)
        }

        return when {
            path.endsWith("/articles") ->
                if (random < timeoutProbability + serverErrorProbability + malformedResponseProbability + emptyArticleListProbability) {
                    buildResponse(request, HTTP_200, json.encodeToString(emptyList<Article>()))
                } else {
                    buildResponse(request, HTTP_200, json.encodeToString(FAKE_ARTICLES))
                }


            path.startsWith("/article/") -> {
                val match = Regex("/article/(\\d+)").find(path)
                val id = match?.groupValues?.get(1)

                val articleDetails = FAKE_ARTICLE_DETAILS.find { it.id == id }
                if (articleDetails != null) {
                    buildResponse(request, HTTP_200, json.encodeToString(articleDetails))
                } else {
                    val str = json.encodeToString(contentNotFoundError)
                    buildResponse(request, HTTP_404, str)
                }
            }

            else -> {
                throw RuntimeException("Invalid path")
            }
        }
    }

    private fun buildResponse(
        request: Request,
        code: Int,
        body: String
    ): Response =
        Response.Builder()
            .request(request)
            .protocol(Protocol.HTTP_1_1)
            .code(code)
            .message(if (code == HTTP_200) "OK" else "Error")
            .addHeader("Content-Type", "application/json")
            .body(body.toResponseBody(jsonMediaType))
            .build()
}

@Serializable
data class ErrorResponse(
    val errorCode: Int,
    val isServerError: Boolean,
    val errorTitle: String,
    val errorMessage: String,
) {
    companion object {
        val UNEXPECTED_RESPONSE = ErrorResponse(
            errorTitle = "Unexpected Response",
            errorMessage = "Received empty or invalid data from server.",
            errorCode = -1,
            isServerError = false
        )
    }
}
