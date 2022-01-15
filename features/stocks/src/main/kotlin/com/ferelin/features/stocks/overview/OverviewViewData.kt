package com.ferelin.features.stocks.overview

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.CryptoId

@Immutable
internal data class CryptoViewData(
  val id: CryptoId,
  val name: String,
  val logoUrl: String,
  val price: String,
  val profit: String
)