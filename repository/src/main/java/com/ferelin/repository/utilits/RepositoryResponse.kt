package com.ferelin.repository.utilits

import com.ferelin.remote.utilits.Api

sealed class RepositoryResponse<out T> {
    data class Success<out T>(
        val data: T,
        val code: Int = Api.RESPONSE_OK
    ) : RepositoryResponse<T>()

    data class Failed<out T>(val code: Int? = null) : RepositoryResponse<T>()
}