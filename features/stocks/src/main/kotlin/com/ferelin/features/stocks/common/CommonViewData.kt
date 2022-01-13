package com.ferelin.features.stocks.common

import androidx.annotation.ColorRes
import com.ferelin.core.domain.entity.CryptoId

data class CryptoViewData(
  val id: CryptoId,
  val name: String,
  val logoUrl: String,
  val price: String,
  val profit: String
)