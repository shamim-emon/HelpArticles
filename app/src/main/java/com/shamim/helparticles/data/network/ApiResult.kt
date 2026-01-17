package com.shamim.helparticles.data.network

sealed class ApiResult<out T> {

    data class Success<T>(
        val data: T
    ) : ApiResult<T>()

    data class Error(
        val errorResponse: ErrorResponse,
    ) : ApiResult<Nothing>()

    object Loading : ApiResult<Nothing>()
}