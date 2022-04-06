package com.ferelin.stockprice.shared.ui.mapper

import com.ferelin.stockprice.shared.domain.entity.Crypto
import com.ferelin.stockprice.shared.domain.entity.CryptoPrice
import com.ferelin.stockprice.shared.ui.formatProfitString
import com.ferelin.stockprice.shared.ui.toStrPrice
import com.ferelin.stockprice.shared.ui.viewData.CryptoViewData

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