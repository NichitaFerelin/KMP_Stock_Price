package com.ferelin.features.stocks.common

import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoPrice
import com.ferelin.core.ui.viewData.utils.formatProfitString
import com.ferelin.core.ui.viewData.utils.toStrPrice

internal object CryptoMapper {
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