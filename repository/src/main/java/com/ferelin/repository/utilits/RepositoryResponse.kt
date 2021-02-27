package com.ferelin.repository.utilits

sealed class RepositoryResponse<out T> {
    data class Success<out T>(val data: T, val code: Int = 200) : RepositoryResponse<T>()
    data class Failed<out T>(val code: Int? = null) : RepositoryResponse<T>()
}