package com.ferelin.stockprice.shared.ui.mapper

import com.ferelin.stockprice.shared.domain.entity.StockPrice
import com.ferelin.stockprice.shared.ui.buildProfitString
import com.ferelin.stockprice.shared.ui.toStrPrice
import com.ferelin.stockprice.shared.ui.viewData.StockPriceViewData

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