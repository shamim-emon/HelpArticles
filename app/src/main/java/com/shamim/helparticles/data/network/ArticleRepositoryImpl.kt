package com.shamim.helparticles.data.network

import com.shamim.cache.SimpleCache
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.model.ArticleDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.SimpleDateFormat
import java.util.Date


class ArticleRepositoryImpl(
    private val apiService: ArticleApiService,
    private val simpleDateFormat: SimpleDateFormat,
    private val json: Json,
    private val articlesCache: SimpleCache<String, List<Article>>,
    private val articleDetailsCache: SimpleCache<String, ArticleDetails>,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ArticleRepository {

    companion object {
        const val ARTICLES_CACHE_KEY = "articles_cache_key"
    }

    override fun getArticles(): Flow<ApiResult<List<Article>>> = flow {
        emit(ApiResult.Loading)
        val cached = articlesCache.get(ARTICLES_CACHE_KEY)
        if (cached != null) {
            emit(ApiResult.Success(cached))
            return@flow
        }

        emitAll(
            makeRequest { apiService.getArticles() }
                .map { result ->
                    if (result is ApiResult.Success) {
                        val formatted = result.data.map {
                            it.copy(
                                formattedDate = simpleDateFormat.format(Date(it.updatedAt))
                            )
                        }
                        if(formatted.isNotEmpty()) {
                            articlesCache.put(ARTICLES_CACHE_KEY, formatted)
                        }


                        ApiResult.Success(formatted)
                    } else result
                }
        )
    }.flowOn(dispatcher)


    override fun getArticleDetails(id: Int): Flow<ApiResult<ArticleDetails>> = flow {
        emit(ApiResult.Loading)
        val cacheKey = id.toString()

        val cached = articleDetailsCache.get(cacheKey)
        if (cached != null) {
            emit(ApiResult.Success(cached))
            return@flow
        }

        emitAll(
            makeRequest { apiService.getArticleDetails(id) }
                .map { result ->
                    if (result is ApiResult.Success) {
                        articleDetailsCache.put(cacheKey, result.data)
                    }
                    result
                }
        )
    }.flowOn(dispatcher)


    private fun <T> makeRequest(apiCall: suspend () -> Response<T>): Flow<ApiResult<T>> = flow {
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error(ErrorResponse.UNEXPECTED_RESPONSE))
        } else {
            emit(ApiResult.Error(response.parseError() ?: ErrorResponse.UNEXPECTED_RESPONSE))
        }
    }.catch { emit(ApiResult.Error(handleThrowable(it))) }


    private fun <T> Response<T>.parseError(): ErrorResponse? {
        val errorJson = this.errorBody()?.string() ?: return null
        return try {
            json.decodeFromString<ErrorResponse>(errorJson)
        } catch (e: Exception) {
            ErrorResponse(
                errorCode = this.code(),
                errorTitle = "Unknown Error",
                errorMessage = errorJson,
                isServerError = this.code() in 500..599,
            )
        }
    }

    private fun handleThrowable(throwable: Throwable): ErrorResponse {
        return when (throwable) {
            is IllegalStateException, is SerializationException -> ErrorResponse(
                errorCode = -1,
                errorTitle = "Malformed Data",
                errorMessage = "Something went wrong while loading the content. Tap retry to try again.",
                isServerError = false
            )

            is SocketTimeoutException -> ErrorResponse(
                errorCode = -1,
                errorTitle = "Timeout",
                errorMessage = "The request is taking too long. Please try again.",
                isServerError = false
            )

            is UnknownHostException -> ErrorResponse(
                errorCode = -1,
                errorTitle = "No Internet",
                errorMessage = "Please check your internet connection.",
                isServerError = false
            )

            else -> ErrorResponse(
                errorCode = -1,
                errorTitle = "Unknown Error",
                errorMessage = throwable.message ?: "Something went wrong.",
                isServerError = false
            )
        }
    }
}