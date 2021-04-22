package com.ferelin.repository.utils

sealed class RepositoryMessages {
    object Error : RepositoryMessages()
    object Limit : RepositoryMessages()
}