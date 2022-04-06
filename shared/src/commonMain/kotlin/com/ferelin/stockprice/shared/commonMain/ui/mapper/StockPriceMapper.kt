package com.ferelin.stockprice.shared.commonMain.ui.mapper

import com.ferelin.stockprice.shared.commonMain.domain.entity.StockPrice
import com.ferelin.stockprice.shared.commonMain.ui.buildProfitString
import com.ferelin.stockprice.shared.commonMain.ui.toStrPrice
import com.ferelin.stockprice.shared.commonMain.ui.viewData.StockPriceViewData

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