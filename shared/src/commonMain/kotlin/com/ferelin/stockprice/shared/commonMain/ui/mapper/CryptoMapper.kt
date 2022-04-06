package com.ferelin.stockprice.shared.commonMain.ui.mapper

import com.ferelin.stockprice.shared.commonMain.domain.entity.Crypto
import com.ferelin.stockprice.shared.commonMain.domain.entity.CryptoPrice
import com.ferelin.stockprice.shared.commonMain.ui.formatProfitString
import com.ferelin.stockprice.shared.commonMain.ui.toStrPrice
import com.ferelin.stockprice.shared.commonMain.ui.viewData.CryptoViewData

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