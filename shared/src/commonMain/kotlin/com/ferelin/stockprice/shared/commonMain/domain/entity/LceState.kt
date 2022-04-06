package com.ferelin.stockprice.shared.commonMain.domain.entity

sealed class LceState {
  object None : LceState()
  object Loading : LceState()
  data class Error(val message: String?) : LceState()
  object Content : LceState()
}