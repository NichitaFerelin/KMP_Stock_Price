package com.ferelin.core.ui.viewData

import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.ui.view.adapter.ViewDataType
import com.ferelin.core.ui.view.stocks.adapter.STOCK_VIEW_TYPE
import com.ferelin.core.ui.viewData.utils.*

data class StockViewData(
  val id: CompanyId,
  val name: String,
  val ticker: String,
  val logoUrl: String,
  val isFavourite: Boolean,
  val style: StockStyle,
  val stockPriceViewData: StockPriceViewData?,
) : ViewDataType(STOCK_VIEW_TYPE) {
  override fun getUniqueId(): Long = id.value.toLong()
}

data class StockStyle(
  @ColorRes val holderBackground: Int = colorHolderSecond,
  @ColorRes val dayProfitBackground: Int = colorProfitPlus,
  @DrawableRes val favouriteBackgroundIcon: Int = drawableFavouriteBackgroundIcon,
  @DrawableRes val favouriteForegroundIcon: Int = drawableFavouriteForegroundIcon,
  @DrawableRes val rippleForeground: Int = drawableRippleLight,
  val iconContentDescription: String = ""
)

data class StockPriceViewData(
  val price: String,
  val profit: String,
  val currentPrice: Double,
  val previousClosePrice: Double
)