package com.ferelin.core.ui.mapper

import com.ferelin.core.ui.viewData.StockPriceViewData
import com.ferelin.core.ui.viewData.utils.buildProfitString
import com.ferelin.core.ui.viewData.utils.toStrPrice
import com.ferelin.stockprice.domain.entity.StockPrice

object StockPriceMapper {
  fun map(stockPrice: StockPrice): StockPriceViewData {
    return StockPriceViewData(
      price = stockPrice.currentPrice.toStrPrice(),
      profit = buildProfitString(
        stockPrice.currentPrice,
        stockPrice.previousClosePrice
      ),
      currentPrice = stockPrice.currentPrice,
      previousClosePrice = stockPrice.previousClosePrice
    )
  }
}