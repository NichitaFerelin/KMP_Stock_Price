package com.ferelin.repository.utils

sealed class RepositoryMessages {
    object Ok : RepositoryMessages()
    object Error : RepositoryMessages()
    object Limit : RepositoryMessages()
}