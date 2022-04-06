package com.ferelin.stockprice.shared.ui.mapper

import com.ferelin.stockprice.androidApp.domain.entity.Crypto
import com.ferelin.stockprice.androidApp.domain.entity.CryptoPrice
import com.ferelin.stockprice.androidApp.ui.formatProfitString
import com.ferelin.stockprice.androidApp.ui.toStrPrice
import com.ferelin.stockprice.androidApp.ui.viewData.CryptoViewData

object CryptoMapper {
  fun map(crypto: Crypto, cryptoPrice: CryptoPrice?): CryptoViewData {
    return CryptoViewData(
      id = crypto.id,
      name = crypto.name,
      logoUrl = crypto.logoUrl,
      price = cryptoPrice?.price?.toStrPrice() ?: "",
      profit = formatProfitString(
        priceProfit = cryptoPrice?.priceChange?.toString() ?: "",
        priceProfitPercents = cryptoPrice?.priceChangePercents?.toString() ?: ""
      ),
    )
  }
}