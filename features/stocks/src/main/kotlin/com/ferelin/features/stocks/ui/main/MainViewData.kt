package com.ferelin.features.stocks.ui.main

import androidx.annotation.ColorRes
import com.ferelin.core.ui.view.adapter.ViewDataType
import com.ferelin.core.domain.entity.CryptoId

internal data class CryptoViewData(
  val id: CryptoId,
  val name: String,
  val logoUrl: String,
  val price: String,
  val profit: String,
  @ColorRes val profitColor: Int
) : ViewDataType(CRYPTO_VIEW_TYPE) {
  override fun getUniqueId(): Long = id.value.toLong()
}