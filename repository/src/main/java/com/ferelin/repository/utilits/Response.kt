package com.ferelin.repository.utilits

sealed class Response<out T> {
    data class Success<out T>(val data: T, val code: Int = 200) : Response<T>()
    data class Failed<out T>(val code: Int? = null) : Response<T>()
}