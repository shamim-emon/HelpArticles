package com.shamim.helparticles.data.network

import com.shamim.helparticles.data.network.ApiResult
import com.shamim.helparticles.data.model.Article
import com.shamim.helparticles.data.model.ArticleDetails
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.lang.IllegalStateException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlinx.serialization.SerializationException



class ArticleRepositoryImpl(
    private val apiService: ArticleApiService,
    private val simpleDateFormat: SimpleDateFormat,
    private val json: Json,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ArticleRepository {


    override fun getArticles(): Flow<ApiResult<List<Article>>> =
        makeRequest { apiService.getArticles() }
            .map { result ->
                if (result is ApiResult.Success) {
                    result.copy(
                        data = result.data.map {
                            it.copy(formattedDate = simpleDateFormat.format(Date(it.updatedAt)))
                        }
                    )
                } else result
            }

    override fun getArticleDetails(id: Int): Flow<ApiResult<ArticleDetails>> =
        makeRequest { apiService.getArticleDetails(id) }

    private fun <T> makeRequest(apiCall: suspend () -> Response<T>): Flow<ApiResult<T>> = flow {
        emit(ApiResult.Loading)
        val response = apiCall()
        if (response.isSuccessful) {
            response.body()?.let {
                emit(ApiResult.Success(it))
            } ?: emit(ApiResult.Error(ErrorResponse.UNEXPECTED_RESPONSE))
        } else {
            emit(ApiResult.Error(response.parseError() ?: ErrorResponse.UNEXPECTED_RESPONSE))
        }
    }.catch { emit(ApiResult.Error(handleThrowable(it))) }
        .flowOn(dispatcher)


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