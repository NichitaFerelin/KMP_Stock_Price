package com.ferelin.repository.utils

import com.ferelin.remote.utilits.Api

sealed class RepositoryResponse<out T> {

    class Success<out T>(
        val owner: String? = null,
        val data: T,
        val code: Int = Api.RESPONSE_OK
    ) : RepositoryResponse<T>()

    class Failed<out T>(val code: Int? = null) : RepositoryResponse<T>()
}