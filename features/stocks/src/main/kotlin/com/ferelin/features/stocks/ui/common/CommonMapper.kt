package com.ferelin.features.stocks.ui.common

import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoPrice
import com.ferelin.core.ui.viewData.utils.formatProfitString
import com.ferelin.core.ui.viewData.utils.toStrPrice
import com.ferelin.features.stocks.R

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
      profitColor = if ((cryptoPrice?.price ?: 0.0) <= 0) profitMinus else profitPlus
    )
  }
}

internal val profitPlus = R.color.profitPlus
internal val profitMinus = R.color.profitMinus