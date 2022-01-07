package com.ferelin.core.domain.entities.entity

sealed class LceState {
  object None : LceState()
  object Loading : LceState()
  data class Error(val message: String?) : LceState()
  object Content : LceState()
}