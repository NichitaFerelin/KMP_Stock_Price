package com.ferelin.stockprice.shared.ui.viewData

import com.ferelin.stockprice.androidApp.domain.entity.CompanyId
import com.ferelin.stockprice.androidApp.domain.entity.CryptoId

data class StockViewData(
  val id: CompanyId,
  val name: String,
  val ticker: String,
  val logoUrl: String,
  val isFavourite: Boolean,
  val stockPriceViewData: StockPriceViewData?
)

data class StockPriceViewData(
  val price: String,
  val profit: String,
  val currentPrice: Double,
  val previousClosePrice: Double
)

data class CryptoViewData(
  val id: CryptoId,
  val name: String,
  val logoUrl: String,
  val price: String,
  val profit: String
)