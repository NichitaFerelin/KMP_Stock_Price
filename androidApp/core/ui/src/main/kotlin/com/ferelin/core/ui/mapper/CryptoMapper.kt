package com.ferelin.core.ui.mapper

import com.ferelin.core.ui.viewData.CryptoViewData
import com.ferelin.core.ui.viewData.utils.formatProfitString
import com.ferelin.core.ui.viewData.utils.toStrPrice
import com.ferelin.stockprice.domain.entity.Crypto
import com.ferelin.stockprice.domain.entity.CryptoPrice

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