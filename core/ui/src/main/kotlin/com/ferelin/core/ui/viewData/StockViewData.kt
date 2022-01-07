package com.ferelin.core.ui.viewData

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.ui.view.adapter.ViewDataType
import com.ferelin.core.ui.view.stocks.adapter.STOCK_VIEW_TYPE

data class StockViewData(
  val id: CompanyId,
  val name: String,
  val ticker: String,
  val logoUrl: String,
  val isFavourite: Boolean,
  val style: StockStyle?,
  val stockPriceViewData: StockPriceViewData?,
) : ViewDataType(STOCK_VIEW_TYPE) {
  override fun getUniqueId(): Long = id.value.toLong()
}

data class StockStyle(
  @ColorRes val holderBackground: Int,
  @DrawableRes val favouriteBackgroundIcon: Int,
  @DrawableRes val favouriteForegroundIcon: Int,
  @ColorRes val dayProfitBackground: Int,
  @ColorRes val rippleForeground: Int,
  val iconContentDescription: String
)

data class StockPriceViewData(
  val price: String,
  val profit: String,
  val currentPrice: Double,
  val previousClosePrice: Double
)