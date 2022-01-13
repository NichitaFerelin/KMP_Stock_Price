package com.ferelin.core.ui.viewData

import com.ferelin.core.domain.entity.CompanyId

data class StockViewData(
  val id: CompanyId,
  val name: String,
  val ticker: String,
  val logoUrl: String,
  val isFavourite: Boolean,
  val stockPriceViewData: StockPriceViewData?,
)

data class StockPriceViewData(
  val price: String,
  val profit: String,
  val currentPrice: Double,
  val previousClosePrice: Double
)