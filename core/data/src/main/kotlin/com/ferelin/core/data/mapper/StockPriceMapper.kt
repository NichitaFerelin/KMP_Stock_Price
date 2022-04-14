package com.ferelin.core.data.mapper

import com.ferelin.core.data.entity.stockPrice.StockPriceResponse
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.StockPrice
import stockprice.StockPriceDBO

internal object StockPriceMapper {
    fun map(stockPrice: StockPrice): StockPriceDBO {
        return StockPriceDBO(
            id = stockPrice.id.value,
            currentPrice = stockPrice.currentPrice,
            previousClosePrice = stockPrice.previousClosePrice,
            openPrice = stockPrice.openPrice,
            highPrice = stockPrice.highPrice,
            lowPrice = stockPrice.lowPrice
        )
    }

    fun map(stockPriceDBO: StockPriceDBO): StockPrice {
        return StockPrice(
            id = CompanyId(stockPriceDBO.id),
            currentPrice = stockPriceDBO.currentPrice,
            previousClosePrice = stockPriceDBO.previousClosePrice,
            openPrice = stockPriceDBO.openPrice,
            highPrice = stockPriceDBO.highPrice,
            lowPrice = stockPriceDBO.lowPrice
        )
    }

    fun map(
        stockPriceResponse: StockPriceResponse,
        companyId: CompanyId
    ): StockPriceDBO {
        return StockPriceDBO(
            id = companyId.value,
            currentPrice = stockPriceResponse.currentPrice,
            previousClosePrice = stockPriceResponse.previousClosePrice,
            openPrice = stockPriceResponse.openPrice,
            highPrice = stockPriceResponse.highPrice,
            lowPrice = stockPriceResponse.lowPrice
        )
    }
}