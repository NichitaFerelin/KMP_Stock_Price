package com.ferelin.core.domain.entity

sealed class LceState {
    object None : LceState()
    object Loading : LceState()
    data class Error(val message: String?) : LceState()
    object Content : LceState()
}

fun LceState.compare(other: LceState): LceState {
    return when {
        this is LceState.Loading || other is LceState.Loading -> LceState.Loading
        this is LceState.Error -> this
        other is LceState.Error -> other
        this is LceState.Content -> this
        else -> other
    }
}