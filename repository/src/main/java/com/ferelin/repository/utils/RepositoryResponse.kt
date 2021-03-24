package com.ferelin.repository.utils

sealed class RepositoryResponse<out T> {

    class Success<out T>(
        val owner: String? = null,
        val data: T
    ) : RepositoryResponse<T>()

    class Failed<out T>(
        val message: RepositoryMessages = RepositoryMessages.Error,
        val owner: String? = null
    ) : RepositoryResponse<T>()
}