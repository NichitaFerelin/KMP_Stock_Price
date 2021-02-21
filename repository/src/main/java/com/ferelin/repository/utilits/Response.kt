package com.ferelin.repository.utilits

sealed class Response<out T> {
    data class Success<out T>(val data: T) : Response<T>()
    data class Failed<out T>(val code: Int? = null) : Response<T>()
}