package com.ferelin.core.ui.viewData

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.CompanyId

@Immutable
data class StockViewData(
  val id: CompanyId,
  val name: String,
  val ticker: String,
  val logoUrl: String,
  val isFavourite: Boolean,
  val stockPriceViewData: StockPriceViewData?
)

@Immutable
data class StockPriceViewData(
  val price: String,
  val profit: String,
  val currentPrice: Double,
  val previousClosePrice: Double
)